package dev.geo.admin.system.model.monitor.dto;

import dev.geo.admin.mybatis.model.page.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * 登录日志分页查询条件。
 */
@Getter
@Setter
@Schema(description = "登录日志分页查询条件")
public class LoginLogPageReqDTO extends PageParam {

    @Schema(description = "用户账号")
    private String userName;

    @Schema(description = "登录状态：0-成功，1-失败")
    private String status;

    @Schema(description = "登录 IP")
    private String ipAddress;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Schema(description = "登录日期起始值")
    private LocalDate beginDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Schema(description = "登录日期结束值")
    private LocalDate endDate;
}
