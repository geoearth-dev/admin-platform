package dev.geo.admin.system.model.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 岗位保存参数。
 */
@Getter
@Setter
@Schema(description = "岗位保存参数")
public class PostSaveDTO {
    @Schema(description = "记录 ID")
    private Long id;

    @NotBlank(message = "岗位编码不能为空")
    @Size(max = 64, message = "岗位编码不能超过64个字符")
    @Schema(description = "岗位编码")
    private String postCode;

    @NotBlank(message = "岗位名称不能为空")
    @Size(max = 50, message = "岗位名称不能超过50个字符")
    @Schema(description = "岗位名称")
    private String postName;

    @NotNull(message = "显示顺序不能为空")
    @Schema(description = "显示顺序，越小越靠前")
    private Integer postSort;

    @Schema(description = "状态：1启用，0停用")
    private String status;
    @Schema(description = "备注")
    private String remark;
}
