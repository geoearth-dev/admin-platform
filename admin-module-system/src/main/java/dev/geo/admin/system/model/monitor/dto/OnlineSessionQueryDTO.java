package dev.geo.admin.system.model.monitor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 在线会话查询条件。
 */
@Getter
@Setter
@Schema(description = "在线会话查询条件")
public class OnlineSessionQueryDTO {
    @Schema(description = "用户账号")
    private String userName;
    @Schema(description = "IP 地址")
    private String ipAddress;
}
