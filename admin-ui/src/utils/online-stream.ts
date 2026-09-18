import { fetchEventSource } from '@microsoft/fetch-event-source';

import { requestClient } from '@/utils/request';

interface OnlineStreamOptions {
  sessionId: string;
  getToken: () => string | null;
  isCurrent: () => boolean;
  refreshToken: () => Promise<string>;
  onInvalidated: (sessionId: string) => Promise<void> | void;
}

interface PingPayload {
  connectionId: string;
  pingId: string;
}

const onlineListeners = new Set<() => void>();

export function subscribeOnlineChanges(listener: () => void) {
  onlineListeners.add(listener);

  return () => {
    onlineListeners.delete(listener);
  };
}

function emitOnlineChanged() {
  for (const listener of onlineListeners) {
    listener();
  }
}

export function startOnlineStream(options: OnlineStreamOptions) {
  const apiBase = (import.meta.env.VITE_API_BASE || '/api').replace(/\/$/, '');

  let stopped = false;
  let activeController: AbortController | undefined;
  let retryTimer: number | undefined;
  let retryDelay = 1000;

  function isActive() {
    return !stopped && options.isCurrent();
  }

  function stop() {
    stopped = true;
    window.clearTimeout(retryTimer);
    activeController?.abort();
  }

  async function invalidate() {
    if (!isActive()) return;

    stop();

    try {
      await options.onInvalidated(options.sessionId);
    } catch (error) {
      console.error('处理会话失效失败', error);
    }
  }

  function connect() {
    if (!isActive()) return;

    const controller = new AbortController();
    activeController = controller;

    let watchdog: number | undefined;
    let firstPong = true;

    function resetWatchdog() {
      window.clearTimeout(watchdog);
      watchdog = window.setTimeout(() => controller.abort(), 45_000);
    }

    function replyPong(payload: PingPayload) {
      void requestClient
        .post<boolean>('/system/messages/pong', payload, {
          silent: true,
          signal: controller.signal,
        })
        .then((accepted) => {
          if (!isActive() || controller.signal.aborted) return;

          if (!accepted) {
            controller.abort();
            return;
          }

          retryDelay = 1000;

          if (firstPong) {
            firstPong = false;
            emitOnlineChanged();
          }
        })
        .catch(() => {
          if (!isActive() || controller.signal.aborted) return;

          controller.abort();
        });
    }

    resetWatchdog();

    void fetchEventSource(`${apiBase}/system/messages/stream`, {
      signal: controller.signal,
      credentials: 'include',
      openWhenHidden: true,

      // 每次请求读取最新 Token；握手 401 时刷新并重试一次。
      async fetch(input, init) {
        const headers = new Headers(init?.headers);
        headers.set('Authorization', `Bearer ${options.getToken() ?? ''}`);

        let response = await fetch(input, { ...init, headers });

        if (response.status === 401 && isActive()) {
          await response.body?.cancel();
          await options.refreshToken();

          if (!isActive() || controller.signal.aborted) {
            throw new DOMException('连接已取消', 'AbortError');
          }

          headers.set('Authorization', `Bearer ${options.getToken() ?? ''}`);
          response = await fetch(input, { ...init, headers });
        }

        return response;
      },

      async onopen(response) {
        if (!response.ok) {
          throw new Error('在线通道连接失败');
        }

        if (!response.headers.get('content-type')?.includes('text/event-stream')) {
          throw new Error('在线通道响应类型错误');
        }

        resetWatchdog();
      },

      onmessage(message) {
        if (!isActive()) return;

        resetWatchdog();

        if (message.event === 'ping') {
          replyPong(JSON.parse(message.data) as PingPayload);
          return;
        }

        if (message.event === 'online-changed') {
          emitOnlineChanged();
          return;
        }

        if (message.event === 'session-invalidated') {
          const event = JSON.parse(message.data) as {
            sessionId: string;
          };

          if (event.sessionId === options.sessionId) {
            void invalidate();
          }
        }
      },

      onerror(error) {
        throw error;
      },
    })
      .catch(async (error) => {
        if (!isActive() || controller.signal.aborted) return;

        if (error.code === 401) {
          await invalidate();
          return;
        }

        if (error.code === 403 || error.code === 404) {
          console.error('请检查在线通道地址和访问配置', error);
          stop();
          return;
        }

        console.debug('在线通道暂时断开，稍后重连', error);
      })
      .finally(() => {
        window.clearTimeout(watchdog);
        controller.abort();

        if (activeController === controller) {
          activeController = undefined;
        }

        if (isActive()) {
          retryTimer = window.setTimeout(connect, retryDelay);
          retryDelay = Math.min(retryDelay * 2, 10_000);
        }
      });
  }

  connect();
  return stop;
}
