package dev.geo.admin.system.model.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

/**
 * 当前登录用户初始化信息。
 */
@Schema(description = "当前登录用户初始化信息")
public record UserInfoVO(
        @Schema(description = "用户Id")
        Long userId,
        @Schema(description = "用户名")
        String username,
        @Schema(description = "用户昵称")
        String nickName,
        @Schema(description = "用户头像")
        String avatar,
        @Schema(description = "权限标识集合")
        Set<String> permissions,

        @Schema(description = "角色标识集合")
        Set<String> roles,

        @Schema(description = "密码自定义配置规则")
        String passwordCharRange,
        @Schema(description = "初始密码是否提醒修改")
        Boolean isDefaultModifyPwd,
        @Schema(description = "密码是否过期")
        Boolean isPasswordExpired

) {
}
