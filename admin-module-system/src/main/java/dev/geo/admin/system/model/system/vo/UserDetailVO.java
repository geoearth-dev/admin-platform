package dev.geo.admin.system.model.system.vo;

import dev.geo.admin.system.model.system.entity.SysPost;
import dev.geo.admin.system.model.system.entity.SysRole;
import dev.geo.admin.system.model.system.entity.SysUser;

import java.util.List;

/**
 * 用户详情及可选角色、岗位信息。
 */
public record UserDetailVO(
        SysUser user,
        List<SysRole> roles,
        List<SysPost> posts,
        List<Long> roleIds,
        List<Long> postIds
) {
}
