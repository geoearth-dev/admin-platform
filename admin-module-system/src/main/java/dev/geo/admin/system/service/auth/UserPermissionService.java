package dev.geo.admin.system.service.auth;

import cn.hutool.core.util.StrUtil;
import dev.geo.admin.common.constant.Constants;
import dev.geo.admin.common.constant.UserConstants;
import dev.geo.admin.system.model.system.entity.SysRole;
import dev.geo.admin.system.model.system.entity.SysUser;
import dev.geo.admin.system.service.system.ISysMenuService;
import dev.geo.admin.system.service.system.ISysRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserPermissionService {

    private final ISysMenuService menuService;
    private final ISysRoleService roleService;

    /**
     * 获取菜单数据权限
     *
     * @param user 用户信息
     * @return 菜单权限信息
     */
    public Set<String> getMenuPermission(SysUser user) {
        if (user.isAdmin()) {
            return Set.of(Constants.ALL_PERMISSION);
        }

        Set<String> authorities = new HashSet<>();
        List<SysRole> roles = user.getRoles();
        if (!CollectionUtils.isEmpty(roles)) {
            // 多角色设置permissions属性，以便数据权限匹配权限
            for (SysRole role : roles) {
                if (StrUtil.equals(role.getStatus(), UserConstants.ROLE_NORMAL) && !role.isAdmin()) {
                    Set<String> rolePermissions = menuService.selectMenuPermsByRoleId(role.getId());
                    role.setPermissions(rolePermissions);
                    authorities.addAll(rolePermissions);
                }
            }
        } else {
            // 菜单权限，例如 system:user:list
            Set<String> menuPermissions = menuService.selectMenuPermsByUserId(user.getId());
            authorities.addAll(menuPermissions);
        }
        return Set.copyOf(authorities);
    }

    /**
     * 获取角色数据权限
     *
     * @param user 用户信息
     * @return 角色权限信息
     */
    public Set<String> getRolePermission(SysUser user) {
        Set<String> roles = new HashSet<String>();
        // 管理员拥有所有权限
        if (user.isAdmin()) {
            roles.add(Constants.SUPER_ADMIN);
        } else {
            roles.addAll(roleService.selectRolePermissionByUserId(user.getId()));
        }
        return roles;
    }
}
