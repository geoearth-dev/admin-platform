package dev.geo.admin.system;

import dev.geo.admin.common.config.AppConfig;
import dev.geo.admin.common.exception.ServiceException;
import dev.geo.admin.security.model.LoginPrincipal;
import dev.geo.admin.security.model.LoginSession;
import dev.geo.admin.security.model.LoginUserInfo;
import dev.geo.admin.security.session.LoginSessionStore;
import dev.geo.admin.system.controller.system.SysProfileController;
import dev.geo.admin.system.model.system.dto.PasswordChangeDTO;
import dev.geo.admin.system.model.system.dto.ProfileUpdateDTO;
import dev.geo.admin.system.model.system.entity.SysUser;
import dev.geo.admin.system.service.system.ISysUserService;
import jakarta.validation.Validation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/** 不修改真实账号，覆盖个人资料、头像、密码及会话边界。 */
class UserProfileTest {
    @TempDir Path directory;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(4);
    private final MemorySessions sessions = new MemorySessions();
    private final SysUser user = new SysUser(7L);
    private SysProfileController controller;
    private boolean phoneUnique = true;
    private boolean saveSucceeds = true;
    private String originalProfile;

    @BeforeEach
    void setUp() {
        originalProfile = AppConfig.getProfile();
        new AppConfig().setProfile(directory.toString());
        user.setUserName("profile-user");
        user.setNickName("旧昵称");
        user.setEmail("old@example.com");
        user.setAvatar("/profile/avatar/original.png");
        user.setPassword(encoder.encode("Old12345"));
        user.setSex("2");
        sessions.create(session("browser", 7L), "hash", Duration.ofHours(1));
        sessions.create(session("mobile", 7L), "hash", Duration.ofHours(1));
        sessions.create(session("other-user", 8L), "hash", Duration.ofHours(1));
        var principal = LoginPrincipal.fromSession(sessions.find("browser").orElseThrow());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
        ISysUserService service = (ISysUserService) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class<?>[]{ISysUserService.class}, (proxy, method, args) -> switch (method.getName()) {
                    case "selectUserById" -> {
                        assertEquals(7L, args[0]);
                        yield user;
                    }
                    case "selectUserRoleGroup" -> "普通用户";
                    case "selectUserPostGroup" -> "开发岗位";
                    case "checkPhoneUnique" -> phoneUnique;
                    case "checkEmailUnique" -> true;
                    case "updateUserProfile" -> {
                        if (!saveSucceeds) yield 0;
                        var update = (SysUser) args[0];
                        assertEquals(7L, update.getId());
                        assertNull(update.getUserName());
                        assertNull(update.getRoleIds());
                        assertNull(update.getDeptId());
                        assertNull(update.getPassword());
                        user.setNickName(update.getNickName());
                        user.setEmail(update.getEmail());
                        user.setPhoneNumber(update.getPhoneNumber());
                        user.setSex(update.getSex());
                        user.setRemark(update.getRemark());
                        yield 1;
                    }
                    case "updateUserAvatar" -> {
                        assertEquals(7L, args[0]);
                        user.setAvatar((String) args[1]);
                        yield true;
                    }
                    case "resetUserPwd" -> {
                        if (!saveSucceeds) yield 0;
                        assertEquals(7L, args[0]);
                        user.setPassword((String) args[1]);
                        yield 1;
                    }
                    default -> throw new UnsupportedOperationException(method.getName());
                });
        controller = new SysProfileController(service, encoder, sessions);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        if (originalProfile != null) new AppConfig().setProfile(originalProfile);
    }

    @Test
    void updatePersistsBackendFieldsAndSynchronizesOnlyThisUsersSessions() {
        var before = sessions.find("browser").orElseThrow();
        var result = controller.updateProfile(new ProfileUpdateDTO(" 新昵称 ", "new@example.com", "", "1", " 备注 "));
        assertEquals(200, result.getCode());
        assertEquals("备注", result.getData().remark());
        assertEquals("", user.getPhoneNumber());
        assertEquals("profile-user", result.getData().userName());
        assertEquals("普通用户", result.getData().roleGroup());
        assertEquals("开发岗位", result.getData().postGroup());
        for (String id : List.of("browser", "mobile")) {
            var updated = sessions.find(id).orElseThrow();
            assertEquals("新昵称", updated.userInfo().nickName());
            assertEquals("new@example.com", updated.userInfo().email());
            assertEquals(user.getAvatar(), updated.userInfo().avatar());
            assertEquals(before.userInfo().permissions(), updated.userInfo().permissions());
        }
        assertEquals(before.expiresAt(), sessions.find("browser").orElseThrow().expiresAt());
        assertEquals("旧昵称", sessions.find("other-user").orElseThrow().userInfo().nickName());
    }

    @Test
    void rejectedProfileDoesNotChangeDatabaseOrSessions() {
        phoneUnique = false;
        var result = controller.updateProfile(new ProfileUpdateDTO("新昵称", "", "13812345678", "2", ""));
        assertNotEquals(200, result.getCode());
        assertEquals("旧昵称", user.getNickName());
        assertEquals("旧昵称", sessions.find("browser").orElseThrow().userInfo().nickName());
    }

    @Test
    void validatesRequiredFieldsFormatsAndRemarkLength() {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            var validator = factory.getValidator();
            assertTrue(validator.validate(new ProfileUpdateDTO("昵称", "", "", "2", "")).isEmpty());
            assertFalse(validator.validate(new ProfileUpdateDTO(" ", "bad-email", "123", "9", "x".repeat(501))).isEmpty());
        }
    }

    @Test
    void passwordRequiresOldPasswordAndRevokesOnlyThisAccountAfterSuccess() {
        assertNotEquals(200, controller.updatePassword(new PasswordChangeDTO("wrong", "New12345")).getCode());
        assertNotEquals(200, controller.updatePassword(new PasswordChangeDTO("Old12345", "Old12345")).getCode());
        assertEquals(3, sessions.findAll().size());
        assertEquals(200, controller.updatePassword(new PasswordChangeDTO("Old12345", "New12345")).getCode());
        assertTrue(encoder.matches("New12345", user.getPassword()));
        assertEquals(List.of("other-user"), sessions.findAll().stream().map(LoginSession::sessionId).toList());
    }

    @Test
    void failedPasswordWriteDoesNotRevokeSessions() {
        saveSucceeds = false;
        assertNotEquals(200, controller.updatePassword(new PasswordChangeDTO("Old12345", "New12345")).getCode());
        assertEquals(3, sessions.findAll().size());
        assertTrue(encoder.matches("Old12345", user.getPassword()));
    }

    @Test
    void avatarIsValidatedStoredAndSynchronized() throws Exception {
        assertThrows(ServiceException.class, () -> controller.updateAvatar(file("fake.png", "not an image".getBytes())));
        assertThrows(ServiceException.class, () -> controller.updateAvatar(file("large.png", new byte[2 * 1024 * 1024 + 1])));
        ByteArrayOutputStream png = new ByteArrayOutputStream();
        ImageIO.write(new BufferedImage(32, 32, BufferedImage.TYPE_INT_RGB), "png", png);
        var result = controller.updateAvatar(file("avatar.png", png.toByteArray()));
        assertEquals(200, result.getCode());
        assertTrue(result.getData().avatar().startsWith("/profile/avatar/"));
        assertEquals(result.getData().avatar(), sessions.find("mobile").orElseThrow().userInfo().avatar());
        assertTrue(Files.exists(directory.resolve(result.getData().avatar().substring("/profile/".length()))));
    }

    private LoginSession session(String id, long userId) {
        var info = new LoginUserInfo(userId, "profile-user", "旧昵称", "old@example.com", "/profile/avatar/original.png",
                null, "127.0.0.1", "内网", "Chrome", "Windows", 1L, "研发", Set.of(), Set.of("profile:read"));
        return new LoginSession(id, info, Instant.now(), Instant.now().plusSeconds(3600), false);
    }

    private MultipartFile file(String name, byte[] bytes) {
        return new MultipartFile() {
            public String getName() { return "file"; }
            public String getOriginalFilename() { return name; }
            public String getContentType() { return "image/png"; }
            public boolean isEmpty() { return bytes.length == 0; }
            public long getSize() { return bytes.length; }
            public byte[] getBytes() { return bytes; }
            public InputStream getInputStream() { return new ByteArrayInputStream(bytes); }
            public void transferTo(File file) throws IOException { Files.write(file.toPath(), bytes); }
        };
    }

    private static class MemorySessions implements LoginSessionStore {
        private final Map<String, LoginSession> values = new LinkedHashMap<>();
        public void create(LoginSession session, String hash, Duration ttl) { values.put(session.sessionId(), session); }
        public Optional<LoginSession> find(String id) { return Optional.ofNullable(values.get(id)); }
        public List<LoginSession> findAll() { return List.copyOf(values.values()); }
        public void updateUserInfo(String id, LoginUserInfo userInfo) {
            values.computeIfPresent(id, (key, session) -> new LoginSession(id, userInfo,
                    session.loginAt(), session.expiresAt(), session.rememberMe()));
        }
        public boolean rotateRefreshToken(String id, String oldHash, String newHash) { return false; }
        public void delete(String id, boolean forced) { values.remove(id); }
    }
}
