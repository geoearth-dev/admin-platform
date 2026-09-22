package dev.geo.admin.system.service.message;

import cn.hutool.core.util.IdUtil;
import dev.geo.admin.security.event.LoginSessionDeletedEvent;
import dev.geo.admin.security.model.LoginSession;
import dev.geo.admin.security.session.LoginSessionStore;
import dev.geo.admin.security.utils.SecurityUtils;
import dev.geo.admin.system.model.message.vo.MessagePushVO;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 维护当前实例上的消息推送连接。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageSseService {
    private static final long HEARTBEAT_INTERVAL = 15_000L;
    private static final long CONNECTION_TIMEOUT = 30 * 60 * 1000L;
    private static final long ONLINE_TIMEOUT = 90_000L;
    private final LoginSessionStore sessionStore;

    private final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();
    // 只由定时任务更新，用于合并在线名单变化通知。
    private Set<String> publishedOnlineIds = Set.of();

    private static final class Connection {
        final String connectionId = IdUtil.fastUUID();
        final String sessionId;
        final SseEmitter emitter;
        final long connectedAt = System.currentTimeMillis();

        volatile LoginSession session;
        volatile long lastPongAt;

        String pendingPingId;

        Connection(LoginSession session, SseEmitter emitter) {
            this.session = session;
            this.sessionId = session.sessionId();
            this.emitter = emitter;
        }
    }

    public SseEmitter connect(String sessionId) {
        LoginSession session = findValidSession(sessionId).orElseThrow(() ->
                new InvalidBearerTokenException("登录会话已失效"));
        SseEmitter emitter = new SseEmitter(CONNECTION_TIMEOUT);
        Connection connection = new Connection(session, emitter);
        emitter.onCompletion(() -> connections.remove(connection.connectionId, connection));
        emitter.onTimeout(() -> close(connection, false));
        emitter.onError(error ->
                connections.remove(connection.connectionId, connection));
        connections.put(connection.connectionId, connection);
        try {
            // 避免会话恰好在连接登记期间被撤销。
            if (findValidSession(sessionId).isEmpty()) {
                close(connection, true);
            } else {
                ping(connection);
            }
        } catch (RuntimeException exception) {
            close(connection, false);
            throw exception;
        }
        return emitter;
    }

    public boolean pong(String sessionId, String connectionId, String pingId) {
        Connection connection = connections.get(connectionId);

        if (connection == null
                || !connection.sessionId.equals(sessionId)) {
            return false;
        }

        synchronized (connection) {
            if (connections.get(connectionId) != connection) {
                return false;
            }

            long now = System.currentTimeMillis();
            long lastActiveAt = connection.lastPongAt == 0
                    ? connection.connectedAt
                    : connection.lastPongAt;

            if (now - lastActiveAt >= ONLINE_TIMEOUT
                    || !connection.session.expiresAt().isAfter(Instant.now())) {
                return false;
            }

            if (connection.pendingPingId == null
                    || !connection.pendingPingId.equals(pingId)) {
                return false;
            }

            connection.pendingPingId = null;
            connection.lastPongAt = now;
            return true;
        }
    }

    public List<LoginSession> onlineSessions() {
        long now = System.currentTimeMillis();
        Instant instant = Instant.now();

        Map<String, LoginSession> sessions = new HashMap<>();

        for (Connection connection : connections.values()) {
            if (connection.lastPongAt > 0
                    && now - connection.lastPongAt < ONLINE_TIMEOUT
                    && connection.session.expiresAt().isAfter(instant)) {
                sessions.put(connection.sessionId, connection.session);
            }
        }

        return List.copyOf(sessions.values());
    }

    @Scheduled(scheduler = "onlineSessionScheduler", fixedDelay = HEARTBEAT_INTERVAL)
    public void heartbeat() {
        for (Connection connection : connections.values()) {
            try {
                Optional<LoginSession> session =
                        findValidSession(connection.sessionId);

                if (session.isEmpty()) {
                    close(connection, true);
                    continue;
                }

                connection.session = session.get();

                long lastActiveAt = connection.lastPongAt == 0
                        ? connection.connectedAt
                        : connection.lastPongAt;

                if (System.currentTimeMillis() - lastActiveAt >= ONLINE_TIMEOUT) {
                    close(connection, false);
                    continue;
                }

                ping(connection);
            } catch (RuntimeException exception) {
                // Redis 暂时异常不能直接判断为登录失效。
                log.warn("SSE 心跳检查失败，sessionId={}",
                        connection.sessionId, exception);
            }
        }

        publishOnlineChanges();
    }

    @EventListener
    public void onSessionDeleted(LoginSessionDeletedEvent event) {
        connections.values().stream()
                .filter(connection ->
                        connection.sessionId.equals(event.sessionId()))
                .forEach(connection -> close(connection, true, event.forced()));
    }

    public void pushUnreadCount(Long userId, long unreadCount) {
        MessagePushVO payload = MessagePushVO.unreadCount(unreadCount);

        for (Connection connection : connections.values()) {
            if (!connection.session.userInfo().userId().equals(userId)) {
                continue;
            }

            try {
                Optional<LoginSession> session =
                        findValidSession(connection.sessionId);

                if (session.isEmpty()) {
                    close(connection, true);
                    continue;
                }

                connection.session = session.get();

                if (SecurityUtils.hasPermission(
                        session.get().userInfo().permissions(),
                        "system:message:list")) {
                    send(connection, payload.type(), payload);
                }
            } catch (RuntimeException exception) {
                log.warn("SSE 消息推送失败，sessionId={}",
                        connection.sessionId, exception);
            }
        }
    }

    private Optional<LoginSession> findValidSession(String sessionId) {
        Instant now = Instant.now();
        return sessionStore.find(sessionId)
                .filter(session -> session.expiresAt().isAfter(now));
    }

    private void ping(Connection connection) {
        synchronized (connection) {
            if (connection.pendingPingId != null
                    || connections.get(connection.connectionId) != connection) {
                return;
            }

            connection.pendingPingId = UUID.randomUUID().toString();

            send(connection, "ping", Map.of(
                    "connectionId", connection.connectionId,
                    "pingId", connection.pendingPingId
            ));
        }
    }

    private void publishOnlineChanges() {
        Set<String> currentIds = onlineSessions().stream()
                .map(LoginSession::sessionId)
                .collect(Collectors.toSet());

        if (currentIds.equals(publishedOnlineIds)) {
            return;
        }

        publishedOnlineIds = currentIds;

        for (Connection connection : connections.values()) {
            if (SecurityUtils.hasPermission(
                    connection.session.userInfo().permissions(),
                    "monitor:online-session:list")) {
                send(connection, "online-changed", Map.of());
            }
        }
    }

    private void send(Connection connection, String event, Object data) {
        if (connections.get(connection.connectionId) != connection) {
            return;
        }

        try {
            connection.emitter.send(
                    SseEmitter.event().name(event).data(data)
            );
        } catch (IOException | IllegalStateException exception) {
            connections.remove(connection.connectionId, connection);
        }
    }

    private void close(Connection connection, boolean invalidated) {
        close(connection, invalidated, false);
    }

    private void close(Connection connection, boolean invalidated, boolean forced) {
        if (!connections.remove(connection.connectionId, connection)) {
            return;
        }

        if (invalidated) {
            try {
                connection.emitter.send(
                        SseEmitter.event()
                                .name("session-invalidated")
                                .data(Map.of(
                                        "forced", forced
                                ))
                );
            } catch (IOException | IllegalStateException exception) {
                // 客户端可能已经断开，仍需完成服务端连接清理。
            }
        }

        connection.emitter.complete();
    }


    @PreDestroy
    public void shutdown() {
        connections.values().forEach(connection -> close(connection, false));
    }


}
