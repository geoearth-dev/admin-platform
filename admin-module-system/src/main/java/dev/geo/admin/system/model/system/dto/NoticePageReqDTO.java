package dev.geo.admin.system.model.system.dto;

import dev.geo.admin.mybatis.model.page.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 通知公告分页查询参数。
 */
@Getter
@Setter
@Schema(description = "通知公告查询条件")
public class NoticePageReqDTO extends PageParam {
    @Schema(description = "公告标题")
    private String noticeTitle;
    @Schema(description = "类型：1通知，2公告")
    private String noticeType;
    @Schema(description = "状态：1启用，0停用")
    private String status;
    @Schema(description = "创建人")
    private String createBy;
}
