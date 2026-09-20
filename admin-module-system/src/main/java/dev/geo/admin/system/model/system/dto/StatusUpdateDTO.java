package dev.geo.admin.system.model.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 通用状态更新参数。
 */
@Getter
@Setter
@Schema(description = "状态修改参数")
public class StatusUpdateDTO {
    @NotNull(message = "主键不能为空")
    @Schema(description = "记录 ID")
    private Long id;

    @NotBlank(message = "状态不能为空")
    @Schema(description = "状态：1启用，0停用")
    private String status;
}
