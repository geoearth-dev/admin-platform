package dev.geo.admin.system.model.monitor.dto;

import dev.geo.admin.mybatis.model.page.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * 操作日志分页查询条件。
 */
@Getter
@Setter
@Schema(description = "操作日志分页查询条件")
public class OperationLogPageReqDTO extends PageParam {

    @Schema(description = "操作模块")
    private String title;

    @Schema(description = "操作人员")
    private String userName;

    @Schema(description = "业务类型")
    private Integer businessType;

    @Schema(description = "业务类型集合")
    private Integer[] businessTypes;

    @Schema(description = "操作状态：0-异常，1-正常")
    private Integer status;

    @Schema(description = "HTTP 请求方法")
    private String httpMethod;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Schema(description = "操作日期起始值")
    private LocalDate beginDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Schema(description = "操作日期结束值")
    private LocalDate endDate;
}
