package dev.geo.admin.system.model.message.vo;

import dev.geo.admin.common.constant.DateTimeFormat;
import dev.geo.admin.excel.annotation.Excel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;


/**
 * 站内消息响应对象。
 */
@Getter
@Setter
@Schema(description = "站内消息")
public class MessageVO {
    @Schema(description = "记录 ID")
    private Long id;

    @Excel(name = "发送人ID")
    @Schema(description = "发送人 ID")
    private Long senderId;

    @Excel(name = "接收人ID")
    @Schema(description = "接收人 ID")
    private Long receiverId;

    @Excel(name = "消息标题")
    @Schema(description = "消息标题")
    private String title;

    @Excel(name = "消息内容")
    @Schema(description = "消息内容")
    private String content;

    @Excel(name = "消息类别")
    @Schema(description = "消息分类")
    private Integer category;

    @Excel(name = "消息级别")
    @Schema(description = "消息级别")
    private Integer messageLevel;

    @Excel(name = "所属模块")
    @Schema(description = "所属业务模块")
    private Integer module;

    @Schema(description = "业务类型")
    private Integer businessType;
    @Schema(description = "业务记录 ID")
    private Long businessId;
    @Schema(description = "关联业务页面地址")
    private String businessUrl;

    @Excel(name = "阅读状态", readConverterExp = "0=未读,1=已读")
    @Schema(description = "阅读状态：0未读，1已读")
    private Integer readStatus;

    @Excel(name = "阅读时间", width = 30, dateFormat = DateTimeFormat.DATE_TIME_PATTERN)
    @Schema(description = "阅读时间")
    private Instant readTime;

    @Excel(name = "创建时间", width = 30, dateFormat = DateTimeFormat.DATE_TIME_PATTERN)
    @Schema(description = "创建时间")
    private Instant createTime;
}
