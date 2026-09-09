package dev.geo.admin.system.model.system.dto;

import dev.geo.admin.common.xss.Xss;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 通知公告保存参数。
 */
@Getter
@Setter
public class NoticeSaveDTO {
    private Long id;

    @Xss(message = "公告标题不能包含脚本字符")
    @NotBlank(message = "公告标题不能为空")
    @Size(max = 50, message = "公告标题不能超过50个字符")
    private String noticeTitle;

    @NotBlank(message = "公告类型不能为空")
    private String noticeType;

    private String noticeContent;
    private String status;
    private String remark;
}
