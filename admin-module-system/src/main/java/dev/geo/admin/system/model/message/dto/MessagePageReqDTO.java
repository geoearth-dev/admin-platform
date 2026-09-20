package dev.geo.admin.system.model.message.dto;

import dev.geo.admin.mybatis.model.page.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 当前用户消息分页查询参数。
 */
@Getter
@Setter
@Schema(description = "消息查询条件")
public class MessagePageReqDTO extends PageParam {
    @Schema(description = "消息标题")
    private String title;
    @Schema(description = "消息分类")
    private Integer category;
    @Schema(description = "消息级别")
    private Integer messageLevel;
    @Schema(description = "所属业务模块")
    private Integer module;
    @Schema(description = "阅读状态：0未读，1已读")
    private Integer readStatus;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Schema(description = "开始时间")
    private Date beginTime;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Schema(description = "结束时间")
    private Date endTime;
}
