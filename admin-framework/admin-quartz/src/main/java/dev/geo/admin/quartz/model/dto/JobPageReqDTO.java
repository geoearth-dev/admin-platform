package dev.geo.admin.quartz.model.dto;

import dev.geo.admin.mybatis.model.page.PageParam;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

/**
 * 任务分页及导出查询参数。
 */
@Getter
@Setter
public class JobPageReqDTO extends PageParam {
    private String jobName;
    private String jobGroup;
    private String invokeTarget;
    @Pattern(regexp = "[01]?", message = "状态只能为0或1")
    private String status;
}
