package dev.geo.admin.quartz.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.geo.admin.excel.annotation.Excel;
import dev.geo.admin.mybatis.model.BaseEntity;
import dev.geo.admin.quartz.util.CronUtils;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 定时任务调度表 sys_job
 *
 * @author ruoyi
 */
@Getter
@Setter
public class SysJob extends BaseEntity {

    /**
     * 任务ID
     */
    @Excel(name = "任务序号", cellType = Excel.ColumnType.NUMERIC)
    private Long id;

    /**
     * 任务名称
     */
    @Excel(name = "任务名称")
    @NotBlank(message = "任务名称不能为空")
    @Size(min = 0, max = 64, message = "任务名称不能超过64个字符")
    private String jobName;

    /**
     * 任务组名
     */
    @Excel(name = "任务组名")
    @Size(max = 64, message = "任务组名不能超过64个字符")
    private String jobGroup;

    /**
     * 调用目标字符串
     */
    @Excel(name = "调用目标字符串")
    @NotBlank(message = "调用目标字符串不能为空")
    @Size(min = 0, max = 500, message = "调用目标字符串长度不能超过500个字符")
    private String invokeTarget;

    /**
     * cron执行表达式
     */
    @Excel(name = "执行表达式 ")
    @NotBlank(message = "Cron执行表达式不能为空")
    @Size(min = 0, max = 255, message = "Cron执行表达式不能超过255个字符")
    private String cronExpression;

    /**
     * cron计划策略
     */
    @Excel(name = "计划策略 ", readConverterExp = "0=默认,1=立即触发执行,2=触发一次执行,3=不触发立即执行")
    @Pattern(regexp = "[0-3]", message = "计划策略只能为0、1、2或3")
    private String misfirePolicy;

    /**
     * 是否并发执行（1允许 0禁止）
     */
    @Excel(name = "并发执行", readConverterExp = "1=允许,0=禁止")
    @Pattern(regexp = "[01]", message = "是否并发只能为0或1")
    private String concurrent;

    /**
     * 任务状态（1正常 0暂停）
     */
    @Excel(name = "任务状态", readConverterExp = "1=正常,0=暂停")
    @Pattern(regexp = "[01]", message = "状态只能为0或1")
    private String status;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getNextValidTime() {
        if (CronUtils.isValid(cronExpression)) {
            return CronUtils.getNextExecution(cronExpression);
        }
        return null;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("jobName", getJobName())
                .append("jobGroup", getJobGroup())
                .append("cronExpression", getCronExpression())
                .append("nextValidTime", getNextValidTime())
                .append("misfirePolicy", getMisfirePolicy())
                .append("concurrent", getConcurrent())
                .append("status", getStatus())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .toString();
    }
}
