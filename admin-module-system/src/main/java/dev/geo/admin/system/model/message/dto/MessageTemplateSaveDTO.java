package dev.geo.admin.system.model.message.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 消息模板保存参数。
 */
@Getter
@Setter
@Schema(description = "消息模板保存参数")
public class MessageTemplateSaveDTO {
    @Schema(description = "记录 ID")
    private Long id;

    @NotBlank(message = "模板编码不能为空")
    @Size(max = 64, message = "模板编码不能超过64个字符")
    @Schema(description = "模板编码")
    private String templateCode;

    @NotBlank(message = "模板名称不能为空")
    @Size(max = 100, message = "模板名称不能超过100个字符")
    @Schema(description = "模板名称")
    private String templateName;

    @NotBlank(message = "消息标题模板不能为空")
    @Size(max = 200, message = "消息标题模板不能超过200个字符")
    @Schema(description = "标题模板")
    private String titleTemplate;

    @NotBlank(message = "消息内容模板不能为空")
    @Schema(description = "内容模板")
    private String contentTemplate;

    @NotNull(message = "消息类别不能为空")
    @Schema(description = "消息分类")
    private Integer category;

    @NotNull(message = "消息级别不能为空")
    @Schema(description = "消息级别")
    private Integer messageLevel;

    @NotBlank(message = "模板状态不能为空")
    @Schema(description = "状态：1启用，0停用")
    private String status;

    @Size(max = 500, message = "备注不能超过500个字符")
    @Schema(description = "备注")
    private String remark;
}
