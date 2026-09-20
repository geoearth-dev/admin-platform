package dev.geo.admin.quartz.model.dto;

import dev.geo.admin.mybatis.model.page.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

/**
 * 任务分页及导出查询参数。
 */
@Getter
@Setter
@Schema(description = "任务查询条件")
public class JobPageReqDTO extends PageParam {
    @Schema(description = "任务名称")
    private String jobName;
    @Schema(description = "任务分组")
    private String jobGroup;
    @Schema(description = "调用目标，如 beanName.methodName()")
    private String invokeTarget;
    @Pattern(regexp = "[01]?", message = "状态只能为0或1")
    @Schema(description = "任务状态：1运行，0暂停")
    private String status;
}
