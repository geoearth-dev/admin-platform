import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { effectScope, nextTick, ref } from 'vue';

const { streams, fetchEventSource, post } = vi.hoisted(() => {
  const browser = new EventTarget();
  Object.assign(browser, {
    setTimeout: (...args: Parameters<typeof setTimeout>) => setTimeout(...args),
    clearTimeout: (id: ReturnType<typeof setTimeout>) => clearTimeout(id),
  });
  vi.stubGlobal('window', browser);
  return {
    streams: [] as Array<import('@microsoft/fetch-event-source').FetchEventSourceInit>,
    fetchEventSource: vi.fn(),
    post: vi.fn(),
  };
});

vi.mock('@microsoft/fetch-event-source', () => ({ fetchEventSource }));
vi.mock('@/utils/request', () => ({ requestClient: { post } }));

import { useOnlineSession } from '@/plugins/effects/hooks/use-online-session';
import { startOnlineStream, subscribeOnlineChanges } from '@/utils/online-stream';

beforeEach(() => {
  vi.useFakeTimers();
  streams.length = 0;
  post.mockReset().mockResolvedValue(true);
  fetchEventSource.mockReset().mockImplementation((_url, options) => {
    streams.push(options);
    return new Promise<void>((resolve) => options.signal.addEventListener('abort', () => resolve()));
  });
});

afterEach(() => {
  vi.useRealTimers();
  vi.restoreAllMocks();
});

function options() {
  return {
    getToken: () => 'access-token',
    refreshToken: vi.fn().mockResolvedValue('new-token'),
    onInvalidated: vi.fn(),
  };
}

function event(name: string, data: unknown) {
  return { event: name, data: JSON.stringify(data), id: '' };
}

describe('在线会话连接', () => {
  it('改密请求期间可暂停连接，请求失败后恢复连接', async () => {
    const enabled = ref(true);
    const scope = effectScope();
    const session = scope.run(() => useOnlineSession({ ...options(), enabled: () => enabled.value }))!;
    expect(streams).toHaveLength(1);
    session.stop();
    expect(streams[0]!.signal!.aborted).toBe(true);
    await vi.advanceTimersByTimeAsync(60_000);
    expect(streams).toHaveLength(1);
    session.start();
    expect(streams).toHaveLength(2);
    expect(streams[1]!.signal!.aborted).toBe(false);
    enabled.value = false;
    await nextTick();
    session.start();
    expect(streams).toHaveLength(2);
    expect(streams[1]!.signal!.aborted).toBe(true);
    scope.stop();
  });

  it('跟随登录状态启停，更新 Token 不重建连接，离开页面清理资源', async () => {
    const enabled = ref(false);
    const token = ref('first-token');
    const scope = effectScope();
    scope.run(() => useOnlineSession({ ...options(), enabled: () => enabled.value, getToken: () => token.value }));
    expect(streams).toHaveLength(0);
    enabled.value = true;
    await nextTick();
    expect(streams).toHaveLength(1);
    token.value = 'refreshed-token';
    await nextTick();
    expect(streams).toHaveLength(1);
    window.dispatchEvent(new Event('pagehide'));
    expect(streams[0]!.signal!.aborted).toBe(true);
    window.dispatchEvent(new Event('pageshow'));
    expect(streams).toHaveLength(2);
    enabled.value = false;
    await nextTick();
    expect(streams[1]!.signal!.aborted).toBe(true);
    scope.stop();
    window.dispatchEvent(new Event('pageshow'));
    expect(streams).toHaveLength(2);
    await vi.advanceTimersByTimeAsync(60_000);
    expect(fetchEventSource).toHaveBeenCalledTimes(2);
  });

  it('回复心跳、通知名单刷新，并区分强退提示', async () => {
    const changed = vi.fn();
    const unsubscribe = subscribeOnlineChanges(changed);
    const callbacks = options();
    const stop = startOnlineStream(callbacks);
    const stream = streams[0]!;
    stream.onmessage!(event('ping', { connectionId: 'connection', pingId: 'ping' }));
    await Promise.resolve();
    expect(post).toHaveBeenCalledWith('/system/messages/pong', { connectionId: 'connection', pingId: 'ping' }, expect.objectContaining({ silent: true }));
    expect(changed).toHaveBeenCalledTimes(1);
    stream.onmessage!(event('online-changed', {}));
    expect(changed).toHaveBeenCalledTimes(2);
    stream.onmessage!(event('session-invalidated', { forced: true }));
    expect(callbacks.onInvalidated).toHaveBeenCalledWith(true);
    expect(stream.signal!.aborted).toBe(true);
    unsubscribe();
    stop();
  });

  it('建立连接携带最新 Token，401 后刷新并重试一次', async () => {
    let token = 'old';
    const callbacks = options();
    callbacks.getToken = () => token;
    callbacks.refreshToken.mockImplementation(async () => { token = 'new'; return token; });
    const fetch = vi.spyOn(globalThis, 'fetch')
      .mockResolvedValueOnce(new Response('', { status: 401 }))
      .mockResolvedValueOnce(new Response('', { headers: { 'content-type': 'text/event-stream' } }));
    const stop = startOnlineStream(callbacks);
    const stream = streams[0]!;
    const response = await stream.fetch!('/stream', {});
    expect(response.ok).toBe(true);
    expect(fetch.mock.calls[0]![1]!.headers).toEqual(expect.any(Headers));
    expect((fetch.mock.calls[1]![1]!.headers as Headers).get('Authorization')).toBe('Bearer new');
    expect(callbacks.refreshToken).toHaveBeenCalledTimes(1);
    await expect(stream.onopen!(new Response('', { status: 403 }))).rejects.toMatchObject({ code: 403 });
    stop();
  });

  it('刷新失败后通知认证层退出并关闭连接', async () => {
    const callbacks = options();
    callbacks.refreshToken.mockRejectedValue(new Error('刷新令牌失效'));
    vi.spyOn(globalThis, 'fetch').mockResolvedValue(new Response('', { status: 401 }));
    const stop = startOnlineStream(callbacks);
    await expect(streams[0]!.fetch!('/stream', {})).rejects.toThrow('登录状态已失效');
    expect(callbacks.onInvalidated).toHaveBeenCalledWith(false);
    expect(streams[0]!.signal!.aborted).toBe(true);
    stop();
  });

  it('断线后重连，主动停止后不再重试', async () => {
    const stop = startOnlineStream(options());
    await vi.advanceTimersByTimeAsync(46_000);
    expect(streams).toHaveLength(2);
    stop();
    await vi.advanceTimersByTimeAsync(60_000);
    expect(streams).toHaveLength(2);
  });
});
