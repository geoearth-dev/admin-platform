package dev.geo.admin.system;

import dev.geo.admin.common.exception.ServiceException;
import dev.geo.admin.security.authentication.SecurityExpression;
import dev.geo.admin.security.event.LoginSessionDeletedEvent;
import dev.geo.admin.security.model.LoginPrincipal;
import dev.geo.admin.security.model.LoginSession;
import dev.geo.admin.security.model.LoginUserInfo;
import dev.geo.admin.security.session.LoginSessionStore;
import dev.geo.admin.system.resolve.config.OnlineSessionConfig;
import dev.geo.admin.system.controller.system.message.MessageController;
import dev.geo.admin.system.controller.monitor.SysUserOnlineController;
import dev.geo.admin.system.model.monitor.dto.OnlineSessionQueryDTO;
import dev.geo.admin.system.service.message.MessageSseService;
import dev.geo.admin.system.service.monitor.impl.SysUserOnlineServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import jakarta.servlet.http.HttpServletResponse;

import java.lang.reflect.Proxy;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/** 不依赖数据库或 Redis，覆盖真实在线服务的会话和权限规则。 */
class OnlineSessionTest {
    private final MemorySessions sessions = new MemorySessions();
    private final MessageSseService connections = new MessageSseService(sessions);
    private final SysUserOnlineServiceImpl online = new SysUserOnlineServiceImpl(sessions, connections);

    @BeforeEach
    void setUp() {
        sessions.events = connections::onSessionDeleted;
    }

    @AfterEach
    void tearDown() {
        connections.shutdown();
        SecurityContextHolder.clearContext();
    }

    @Test
    void ordinaryUserCanConnectWithoutMessagePermissionButAnonymousCannot() {
        try (var context = new AnnotationConfigApplicationContext()) {
            context.register(MethodSecurity.class);
            context.registerBean("se", SecurityExpression.class, SecurityExpression::new);
            context.registerBean(MessageController.class, () -> new MessageController(null, connections, null));
            context.registerBean(SysUserOnlineController.class, () -> new SysUserOnlineController(online));
            context.refresh();
            var controller = context.getBean(MessageController.class);
            var session = session("ordinary", 2L, "user");
            sessions.put(session);
            var authentication = authenticate(session);
            var response = (HttpServletResponse) Proxy.newProxyInstance(
                    getClass().getClassLoader(), new Class<?>[]{HttpServletResponse.class},
                    (proxy, method, args) -> null);
            assertNotNull(controller.stream(authentication, response));
            var onlineController = context.getBean(SysUserOnlineController.class);
            assertThrows(AccessDeniedException.class, () -> onlineController.list(new OnlineSessionQueryDTO()));
            assertThrows(AccessDeniedException.class, () -> onlineController.terminateSession("target"));
            SecurityContextHolder.clearContext();
            assertThrows(org.springframework.security.core.AuthenticationException.class,
                    () -> controller.stream(authentication, response));
        }
    }

    @Test
    void onlineListRequiresHeartbeatAndMergesConnectionsOfTheSameSession() throws Exception {
        var session = session("user-browser", 2L, "user");
        sessions.put(session);
        connections.connect(session.sessionId());
        assertTrue(connections.onlineSessions().isEmpty());
        var connection = connectionMap().values().iterator().next();
        assertFalse(connections.pong("another-session", value(connection, "connectionId"), value(connection, "pendingPingId")));
        confirmHeartbeat(connection);
        connectOnline(session);
        assertEquals(1, connections.onlineSessions().size());
        authenticate(session("operator", 1L, "admin"));
        var query = new OnlineSessionQueryDTO();
        query.setUserName("USER");
        var result = online.getOnlineSessions(query);
        assertEquals("user-browser", result.get(0).getSessionId());
        assertEquals(2L, result.get(0).getUserId());
        query.setIpAddress("no-match");
        assertTrue(online.getOnlineSessions(query).isEmpty());
    }

    @Test
    void timeoutClosesConnectionWithoutRevokingLogin() throws Exception {
        var session = session("timeout", 2L, "user");
        var connection = connectOnline(session);
        setValue(connection, "lastPongAt", System.currentTimeMillis() - 91_000);
        connections.heartbeat();
        assertTrue(connections.onlineSessions().isEmpty());
        assertTrue(connectionMap().isEmpty());
        assertTrue(sessions.find(session.sessionId()).isPresent());
    }

    @Test
    void forceLogoutRevokesOnlyTheSelectedSession() throws Exception {
        var selected = session("selected", 2L, "user");
        connectOnline(selected);
        connectOnline(selected);
        connectOnline(session("other-device", 2L, "user"));
        authenticate(session("operator", 1L, "admin"));
        online.terminateSession("selected");
        assertTrue(sessions.find("selected").isEmpty());
        assertTrue(sessions.find("other-device").isPresent());
        assertTrue(sessions.lastEvent.forced());
        assertEquals(List.of("other-device"), connections.onlineSessions().stream().map(LoginSession::sessionId).toList());
        assertDoesNotThrow(() -> online.terminateSession("selected"));
    }

    @Test
    void currentSessionAndSuperAdministratorAreProtected() throws Exception {
        var admin = session("admin-browser", 1L, "admin");
        var operator = session("operator", 2L, "user");
        connectOnline(admin);
        connectOnline(operator);
        authenticate(operator);
        connectOnline(session("peer", 3L, "peer"));
        assertDoesNotThrow(() -> online.terminateSession("peer"));
        assertThrows(ServiceException.class, () -> online.terminateSession("operator"));
        assertThrows(ServiceException.class, () -> online.terminateSession("admin-browser"));
        var rows = online.getOnlineSessions(new OnlineSessionQueryDTO());
        assertTrue(rows.stream().noneMatch(row -> row.isForceLogoutAllowed()));
        assertTrue(rows.stream().filter(row -> row.getSessionId().equals("operator")).findFirst().orElseThrow().isCurrentSession());
        authenticate(session("admin-other-browser", 1L, "admin"));
        assertDoesNotThrow(() -> online.terminateSession("admin-browser"));
    }

    @Test
    void heartbeatIsRegisteredOnItsOwnScheduler() {
        try (var context = new AnnotationConfigApplicationContext()) {
            context.register(OnlineSessionConfig.class);
            context.registerBean(MessageSseService.class, () -> connections);
            context.refresh();
            assertNotNull(context.getBean("onlineSessionScheduler"));
            assertEquals(1, context.getBean(ScheduledAnnotationBeanPostProcessor.class).getScheduledTasks().size());
        }
    }

    private Object connectOnline(LoginSession session) throws Exception {
        sessions.put(session);
        Set<String> previousIds = new HashSet<>(connectionMap().keySet());
        connections.connect(session.sessionId());
        Object connection = connectionMap().entrySet().stream()
                .filter(entry -> !previousIds.contains(entry.getKey())).findFirst().orElseThrow().getValue();
        confirmHeartbeat(connection);
        return connection;
    }

    private void confirmHeartbeat(Object connection) throws Exception {
        assertTrue(connections.pong(value(connection, "sessionId"), value(connection, "connectionId"), value(connection, "pendingPingId")));
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> connectionMap() throws Exception {
        return (Map<String, Object>) value(connections, "connections");
    }

    @SuppressWarnings("unchecked")
    private static <T> T value(Object object, String name) throws Exception {
        var field = object.getClass().getDeclaredField(name);
        field.setAccessible(true);
        return (T) field.get(object);
    }

    private static void setValue(Object object, String name, Object value) throws Exception {
        var field = object.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(object, value);
    }

    private static LoginSession session(String id, long userId, String username) {
        var user = new LoginUserInfo(userId, username, username, null, null, null,
                "127.0.0.1", "内网", "Chrome", "Windows", 10L, "研发部", Set.of(), Set.of());
        return new LoginSession(id, user, Instant.now(), Instant.now().plusSeconds(3600), false);
    }

    private static JwtAuthenticationToken authenticate(LoginSession session) {
        var principal = LoginPrincipal.fromSession(session);
        var jwt = Jwt.withTokenValue("test").header("alg", "HS256")
                .subject(principal.getUsername()).jti(session.sessionId()).build();
        var authentication = new JwtAuthenticationToken(jwt, principal, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }

    @EnableMethodSecurity
    static class MethodSecurity {}

    private static final class MemorySessions implements LoginSessionStore {
        private final Map<String, LoginSession> values = new HashMap<>();
        private java.util.function.Consumer<LoginSessionDeletedEvent> events;
        private LoginSessionDeletedEvent lastEvent;

        void put(LoginSession session) { values.put(session.sessionId(), session); }
        public void create(LoginSession session, String hash, Duration ttl) { put(session); }
        public Optional<LoginSession> find(String id) { return Optional.ofNullable(values.get(id)); }
        public List<LoginSession> findAll() { return List.copyOf(values.values()); }
        public void updateUserInfo(String id, LoginUserInfo userInfo) {
            values.computeIfPresent(id, (key, session) -> new LoginSession(key, userInfo,
                    session.loginAt(), session.expiresAt(), session.rememberMe()));
        }
        public boolean rotateRefreshToken(String id, String oldHash, String newHash) { return false; }
        public void delete(String id, boolean forced) {
            values.remove(id);
            lastEvent = new LoginSessionDeletedEvent(id, forced);
            events.accept(lastEvent);
        }
    }
}
