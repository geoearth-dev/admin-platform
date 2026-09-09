package dev.geo.admin.security.model;

import java.time.Instant;
import java.util.Set;

public record LoginUserInfo(
        /*
          用户ID
         */
        Long userId,
        String username,
        String nickName,
        String avatar,

        Instant passwordUpdateTime,
        /*
         * 登录IP地址
         */
        String ip,
        /*
         * 登录地点
         */
        String loginLocation,
        /*
         * 浏览器类型
         */
        String browser,
        /*
         * 操作系统
         */
        String os,
        /*
          部门ID
         */
        Long deptId,
        String deptName,
        Set<RoleGrant> roleGrants,
        /*
          权限列表
         */
        Set<String> permissions
) {
    public LoginUserInfo {
        roleGrants = roleGrants == null ? Set.of() : Set.copyOf(roleGrants);

        permissions = permissions == null ? Set.of() : Set.copyOf(permissions);
    }
}
