package dev.geo.admin.system.model.message.vo;

import dev.geo.admin.common.constant.DateTimeFormat;
import dev.geo.admin.excel.annotation.Excel;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;


/**
 * 消息模板响应对象。
 */
@Getter
@Setter
public class MessageTemplateVO {
    private Long id;

    @Excel(name = "模板编码")
    private String templateCode;

    @Excel(name = "模板名称")
    private String templateName;

    @Excel(name = "标题模板")
    private String titleTemplate;

    @Excel(name = "内容模板")
    private String contentTemplate;

    @Excel(name = "消息类别")
    private Integer category;

    @Excel(name = "消息级别")
    private Integer messageLevel;

    @Excel(name = "状态", readConverterExp = "0=启用,1=停用")
    private String status;

    @Excel(name = "创建时间", width = 30, dateFormat = DateTimeFormat.DATE_TIME_PATTERN)
    private Instant createTime;

    private String remark;
}
