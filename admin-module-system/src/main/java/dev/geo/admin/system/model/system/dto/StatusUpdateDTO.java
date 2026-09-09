package dev.geo.admin.system.model.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 通用状态更新参数。
 */
@Getter
@Setter
public class StatusUpdateDTO {
    @NotNull(message = "主键不能为空")
    private Long id;

    @NotBlank(message = "状态不能为空")
    private String status;
}
