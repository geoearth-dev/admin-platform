package dev.geo.admin.system.model.system.vo;

import dev.geo.admin.system.model.system.entity.SysRole;
import dev.geo.admin.system.model.system.entity.SysUser;

import java.util.List;

/**
 * 用户角色授权信息。
 */
public record UserRoleGrantVO(SysUser user, List<SysRole> roles) {
}
