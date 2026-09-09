package dev.geo.admin.security.model;

import jakarta.annotation.Nullable;
import lombok.Getter;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Spring Security 当前认证主体
 */
@NullMarked
public final class LoginPrincipal implements UserDetails, CredentialsContainer {

    @Getter
    private final LoginUserInfo userInfo;

    private @Nullable String password;

    public LoginPrincipal(LoginUserInfo userInfo, @Nullable String password) {
        this.userInfo = userInfo;
        this.password = password;
    }

    /**
     * 从 Redis 登录会话恢复认证主体。
     */
    public static LoginPrincipal fromSession(LoginSession session) {
        return new LoginPrincipal(session.userInfo(), null);
    }

    public Long getUserId() {
        return userInfo.userId();
    }

    public String getNickName() {
        return userInfo.nickName();
    }
    public String getAvatar() {
        return userInfo.avatar();
    }

    public Instant getPasswordUpdateTime() {
        return userInfo.passwordUpdateTime();
    }

    public String getIp() {
        return userInfo.ip();
    }

    public @Nullable Long getDeptId() {
        return userInfo.deptId();
    }

    public @Nullable String getDeptName() {
        return userInfo.deptName();
    }

    public Set<RoleGrant> getRoleGrants() {
        return userInfo.roleGrants();
    }

    public Set<String> getPermissions() {
        return userInfo.permissions();
    }

    public Set<String> getRolesKey() {
        return userInfo.roleGrants()
                .stream()
                .map(RoleGrant::roleKey)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Stream<String> permissionStream = userInfo.permissions().stream();
        Stream<String> roleStream = getRolesKey().stream().map(role -> "ROLE_" + role);
        return Stream.concat(permissionStream, roleStream).distinct().map(SimpleGrantedAuthority::new).toList();
    }

    @Override
    public @Nullable String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userInfo.username();
    }

    /**
     * 账户是否未过期,过期无法验证
     */
    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    /**
     * 指定用户是否解锁,锁定的用户无法进行身份验证
     */
    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    /**
     * 指示是否已过期的用户的凭据(密码),过期的凭据防止认证
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    /**
     * 是否可用 ,禁用的用户不能身份验证
     */
    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    /**
     * 认证完成后清除密码。
     */
    @Override
    public void eraseCredentials() {
        password = null;
    }

}
