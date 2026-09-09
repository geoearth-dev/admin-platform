package dev.geo.admin.system.resolve.aspect;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import dev.geo.admin.common.annotation.DataScope;
import dev.geo.admin.mybatis.model.BaseEntity;
import dev.geo.admin.security.model.LoginPrincipal;
import dev.geo.admin.security.model.RoleGrant;
import dev.geo.admin.security.utils.SecurityUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 数据过滤处理
 */
@Aspect
@Component
public class DataScopeAspect {
    /**
     * 全部数据权限
     */
    public static final String DATA_SCOPE_ALL = "1";

    /**
     * 自定数据权限
     */
    public static final String DATA_SCOPE_CUSTOM = "2";

    /**
     * 部门数据权限
     */
    public static final String DATA_SCOPE_DEPT = "3";

    /**
     * 部门及以下数据权限
     */
    public static final String DATA_SCOPE_DEPT_AND_CHILD = "4";

    /**
     * 仅本人数据权限
     */
    public static final String DATA_SCOPE_SELF = "5";

    /**
     * 数据权限过滤关键字
     */
    public static final String DATA_SCOPE = "dataScope";

    @Before("@annotation(controllerDataScope)")
    public void doBefore(JoinPoint point, DataScope controllerDataScope) {
        // 防止调用方通过params自行注入dataScope SQL
        clearDataScope(point);
        handleDataScope(point, controllerDataScope);
    }

    protected void handleDataScope(final JoinPoint joinPoint, DataScope controllerDataScope) {
        // 获取当前的用户
        LoginPrincipal principal = SecurityUtils.getLoginPrincipal();
        // 如果是超级管理员，则不过滤数据
        if (SecurityUtils.isAdmin(principal.getUserId())) {
            return;
        }
        String permission = ObjectUtil.defaultIfEmpty(controllerDataScope.permission(), CollUtil.join(principal.getPermissions(), ","));
        dataScopeFilter(
                joinPoint,
                principal,
                controllerDataScope.deptAlias(),
                controllerDataScope.deptField(),
                controllerDataScope.userAlias(),
                controllerDataScope.userField(),
                permission
        );

    }

    /**
     * 数据范围过滤
     *
     * @param joinPoint  切点
     * @param user       用户
     * @param deptAlias  部门别名
     * @param deptField  部门主键字段
     * @param userAlias  用户别名
     * @param userField  用户表主键字段
     * @param permission 权限字符
     */
    public void dataScopeFilter(JoinPoint joinPoint, LoginPrincipal user, String deptAlias, String deptField,
                                String userAlias, String userField, String permission) {
        StringBuilder sqlString = new StringBuilder();
        List<String> conditions = new ArrayList<>();
        List<String> scopeCustomIds = new ArrayList<>();
        Set<RoleGrant> roles = user.getRoleGrants();
        for (RoleGrant role : roles) {
            if (DATA_SCOPE_CUSTOM.equals(role.dataScope()) && CollUtil.containsAny(role.permissions(), StrUtil.split(permission, StrUtil.COMMA))) {
                scopeCustomIds.add(Convert.toStr(role.roleId()));
            }
        }

        for (RoleGrant role : roles) {
            String dataScope = role.dataScope();
            if (conditions.contains(dataScope)) {
                continue;
            }
            if (!CollUtil.containsAny(role.permissions(), StrUtil.split(permission, StrUtil.COMMA))) {
                continue;
            }
            if (DATA_SCOPE_ALL.equals(dataScope)) {
                sqlString = new StringBuilder();
                conditions.add(dataScope);
                break;
            } else if (DATA_SCOPE_CUSTOM.equals(dataScope)) {
                if (scopeCustomIds.size() > 1) {
                    // 多个自定数据权限使用in查询，避免多次拼接。
                    sqlString.append(StrUtil.format(" OR {}.{} IN (SELECT dept_id FROM sys_role_dept WHERE role_id IN ({})) ",
                            deptAlias, deptField, String.join(",", scopeCustomIds)));
                } else {
                    sqlString.append(StrUtil.format(" OR {}.{} IN (SELECT dept_id FROM sys_role_dept WHERE role_id = {}) ",
                            deptAlias, deptField, role.roleId()));
                }
            } else if (DATA_SCOPE_DEPT.equals(dataScope)) {
                sqlString.append(StrUtil.format(" OR {}.{} = {} ", deptAlias, deptField, user.getDeptId()));
            } else if (DATA_SCOPE_DEPT_AND_CHILD.equals(dataScope)) {
                sqlString.append(StrUtil.format(" OR {}.{} IN (SELECT id FROM sys_dept WHERE id = {} OR find_in_set({}, ancestors)) ",
                        deptAlias, deptField, user.getDeptId(), user.getDeptId()));
            } else if (DATA_SCOPE_SELF.equals(dataScope)) {
                if (StrUtil.isNotBlank(userAlias)) {
                    sqlString.append(StrUtil.format(" OR {}.{} = {} ", userAlias, userField, user.getUserId()));
                } else {
                    // 数据权限为仅本人且没有userAlias别名不查询任何数据
                    sqlString.append(StrUtil.format(" OR {}.{} = 0 ", deptAlias, deptField));
                }
            }
            conditions.add(dataScope);
        }

        // 角色都不包含传递过来的权限字符，这个时候sqlString也会为空，所以要限制一下,不查询任何数据
        if (CollUtil.isEmpty(conditions)) {
            sqlString.append(StrUtil.format(" OR {}.{} = 0 ", deptAlias, deptField));
        }

        if (StrUtil.isNotBlank(sqlString.toString())) {
            Object params = joinPoint.getArgs()[0];
            if (ObjectUtil.isNotNull(params) && params instanceof BaseEntity baseEntity) {
                baseEntity.getParams().put(DATA_SCOPE, " AND (" + sqlString.substring(4) + ")");
            }
        }
    }

    /**
     * 拼接权限sql前先清空params.dataScope参数防止注入
     */
    private void clearDataScope(final JoinPoint joinPoint) {
        Object params = joinPoint.getArgs()[0];
        if (ObjectUtil.isNotNull(params) && params instanceof BaseEntity baseEntity) {
            baseEntity.getParams().put(DATA_SCOPE, "");
        }
    }
}
