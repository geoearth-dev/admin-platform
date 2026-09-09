package dev.geo.admin.system.model.message.vo;

import dev.geo.admin.common.constant.DateTimeFormat;
import dev.geo.admin.excel.annotation.Excel;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;


/**
 * 站内消息响应对象。
 */
@Getter
@Setter
public class MessageVO {
    private Long id;

    @Excel(name = "发送人ID")
    private Long senderId;

    @Excel(name = "接收人ID")
    private Long receiverId;

    @Excel(name = "消息标题")
    private String title;

    @Excel(name = "消息内容")
    private String content;

    @Excel(name = "消息类别")
    private Integer category;

    @Excel(name = "消息级别")
    private Integer messageLevel;

    @Excel(name = "所属模块")
    private Integer module;

    private Integer businessType;
    private Long businessId;
    private String businessUrl;

    @Excel(name = "阅读状态", readConverterExp = "0=未读,1=已读")
    private Integer readStatus;

    @Excel(name = "阅读时间", width = 30, dateFormat = DateTimeFormat.DATE_TIME_PATTERN)
    private Instant readTime;

    @Excel(name = "创建时间", width = 30, dateFormat = DateTimeFormat.DATE_TIME_PATTERN)
    private Instant createTime;
}
