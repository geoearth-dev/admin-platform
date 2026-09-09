package dev.geo.admin.system.model.monitor.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 在线会话查询条件。
 */
@Getter
@Setter
public class OnlineSessionQueryDTO {
    private String userName;
    private String ipAddress;
}
