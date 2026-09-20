package dev.geo.admin.system.model.message.vo;

import dev.geo.admin.common.constant.DateTimeFormat;
import dev.geo.admin.excel.annotation.Excel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;


/**
 * 消息模板响应对象。
 */
@Getter
@Setter
@Schema(description = "消息模板")
public class MessageTemplateVO {
    @Schema(description = "记录 ID")
    private Long id;

    @Excel(name = "模板编码")
    @Schema(description = "模板编码")
    private String templateCode;

    @Excel(name = "模板名称")
    @Schema(description = "模板名称")
    private String templateName;

    @Excel(name = "标题模板")
    @Schema(description = "标题模板")
    private String titleTemplate;

    @Excel(name = "内容模板")
    @Schema(description = "内容模板")
    private String contentTemplate;

    @Excel(name = "消息类别")
    @Schema(description = "消息分类")
    private Integer category;

    @Excel(name = "消息级别")
    @Schema(description = "消息级别")
    private Integer messageLevel;

    @Excel(name = "状态", readConverterExp = "0=停用,1=启用")
    @Schema(description = "状态：1启用，0停用")
    private String status;

    @Excel(name = "创建时间", width = 30, dateFormat = DateTimeFormat.DATE_TIME_PATTERN)
    @Schema(description = "创建时间")
    private Instant createTime;

    @Schema(description = "备注")
    private String remark;
}
