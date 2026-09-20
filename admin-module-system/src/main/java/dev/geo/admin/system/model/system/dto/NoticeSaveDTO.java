package dev.geo.admin.system.model.system.dto;

import dev.geo.admin.common.xss.Xss;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 通知公告保存参数。
 */
@Getter
@Setter
@Schema(description = "通知公告保存参数")
public class NoticeSaveDTO {
    @Schema(description = "记录 ID")
    private Long id;

    @Xss(message = "公告标题不能包含脚本字符")
    @NotBlank(message = "公告标题不能为空")
    @Size(max = 50, message = "公告标题不能超过50个字符")
    @Schema(description = "公告标题")
    private String noticeTitle;

    @NotBlank(message = "公告类型不能为空")
    @Schema(description = "类型：1通知，2公告")
    private String noticeType;

    @Schema(description = "公告内容")
    private String noticeContent;

    @Schema(description = "外链地址")
    private String link;

    @Size(max = 2048, message = "头像地址不能超过2048个字符")
    @Schema(description = "头像地址")
    private String avatar;
    @Schema(description = "状态：1启用，0停用")
    private String status;
    @Schema(description = "备注")
    private String remark;
}
