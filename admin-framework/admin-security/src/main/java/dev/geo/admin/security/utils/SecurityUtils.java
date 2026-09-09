package dev.geo.admin.security.utils;

import cn.hutool.core.util.StrUtil;
import dev.geo.admin.common.constant.Constants;
import dev.geo.admin.common.constant.HttpStatus;
import dev.geo.admin.common.exception.ServiceException;
import dev.geo.admin.security.model.LoginPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.PatternMatchUtils;

import java.util.Collection;

/**
 * 安全服务工具类
 */
public class SecurityUtils {

    /**
     * 用户ID
     **/
    public static Long getUserId() {
        try {
            return getLoginPrincipal().getUserId();
        } catch (Exception e) {
            throw new ServiceException("获取用户ID异常", HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * 获取部门ID
     **/
    public static Long getDeptId() {
        try {
            return getLoginPrincipal().getDeptId();
        } catch (Exception e) {
            throw new ServiceException("获取部门ID异常", HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * 获取用户账户
     **/
    public static String getUsername() {
        try {
            return getLoginPrincipal().getUsername();
        } catch (Exception e) {
            throw new ServiceException("获取用户账户异常", HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * 获取用户
     **/
    public static LoginPrincipal getLoginPrincipal() {
        try {
            return (LoginPrincipal) getAuthentication().getPrincipal();
        } catch (Exception e) {
            throw new ServiceException("获取用户信息异常", HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * 获取Authentication
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 生成BCryptPasswordEncoder密码
     *
     * @param password 密码
     * @return 加密字符串
     */
    public static String encryptPassword(String password) {
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    /**
     * 判断密码是否相同
     *
     * @param rawPassword     真实密码
     * @param encodedPassword 加密后字符
     * @return 结果
     */
    public static boolean matchesPassword(String rawPassword, String encodedPassword) {
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * 是否为管理员
     *
     * @return 结果
     */
    public static boolean isAdmin() {
        return isAdmin(getUserId());
    }

    /**
     * 是否为管理员
     *
     * @param userId 用户ID
     * @return 结果
     */
    public static boolean isAdmin(Long userId) {
        return userId != null && 1L == userId;
    }

    /**
     * 验证用户是否具备某权限
     *
     * @param permission 权限字符串
     * @return 用户是否具备某权限
     */
    public static boolean hasPermission(String permission) {
        return hasPermission(getLoginPrincipal().getPermissions(), permission);
    }

    /**
     * 判断是否包含权限,包含通配符
     *
     * @param authorities 权限列表
     * @param permission  权限字符串
     * @return 用户是否具备某权限
     */
    public static boolean hasPermission(Collection<String> authorities, String permission) {
        return authorities.stream()
                .filter(StrUtil::isNotBlank)
                .anyMatch(authority ->
                        Constants.ALL_PERMISSION.equals(authority) || PatternMatchUtils.simpleMatch(authority, permission)
                );
    }

    /**
     * 验证用户是否拥有某个角色
     *
     * @param role 角色标识
     * @return 用户是否具备某角色
     */
    public static boolean hasRole(String role) {
        return hasRole(getLoginPrincipal().getRolesKey(), role);
    }

    /**
     * 判断是否包含角色
     *
     * @param roles 角色列表
     * @param role  角色
     * @return 用户是否具备某角色权限
     */
    public static boolean hasRole(Collection<String> roles, String role) {
        return roles.stream().filter(StrUtil::isNotBlank)
                .anyMatch(x -> Constants.SUPER_ADMIN.equals(x) || PatternMatchUtils.simpleMatch(x, role));
    }

}
