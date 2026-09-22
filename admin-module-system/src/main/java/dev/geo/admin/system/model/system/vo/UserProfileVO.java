package dev.geo.admin.system.model.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/** 当前用户可查看的个人资料，不包含密码和授权编辑字段。 */
@Schema(description = "当前用户个人资料")
public record UserProfileVO(
        Long id,
        String userName,
        String nickName,
        String email,
        String phoneNumber,
        String sex,
        String avatar,
        String remark,
        String deptName,
        String roleGroup,
        String postGroup,
        Instant createTime
) {
}
