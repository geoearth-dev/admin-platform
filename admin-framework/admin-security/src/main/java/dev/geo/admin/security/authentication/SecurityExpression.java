package dev.geo.admin.security.authentication;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

import dev.geo.admin.common.constant.Constants;
import dev.geo.admin.security.model.LoginPrincipal;
import dev.geo.admin.security.utils.SecurityUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Set;

@Component("se")
public class SecurityExpression {
    /**
     * 验证用户是否具备某权限
     *
     * @param permission 权限字符串
     * @return 用户是否具备某权限
     */
    public boolean hasPermission(String permission) {
        if (StrUtil.isEmpty(permission)) {
            return false;
        }
        LoginPrincipal loginPrincipal = SecurityUtils.getLoginPrincipal();
        if (ObjectUtil.isNull(loginPrincipal) || CollectionUtils.isEmpty(loginPrincipal.getPermissions())) {
            return false;
        }
        return hasPermission(loginPrincipal.getPermissions(), permission);
    }

    /**
     * 判断是否包含权限，精确匹配
     *
     * @param permissions 权限列表
     * @param permission  权限字符串
     * @return 用户是否具备某权限
     */
    private boolean hasPermission(Set<String> permissions, String permission) {
        return permissions.contains(Constants.ALL_PERMISSION) || permissions.contains(StrUtil.trim(permission));
    }
}
