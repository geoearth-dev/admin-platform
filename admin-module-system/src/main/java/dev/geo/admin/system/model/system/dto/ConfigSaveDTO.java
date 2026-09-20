package dev.geo.admin.system.model.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 系统参数保存参数。
 */
@Getter
@Setter
@Schema(description = "系统参数保存参数")
public class ConfigSaveDTO {
    @Schema(description = "记录 ID")
    private Long id;

    @NotBlank(message = "参数名称不能为空")
    @Size(max = 100, message = "参数名称不能超过100个字符")
    @Schema(description = "参数名称")
    private String configName;

    @NotBlank(message = "参数键名不能为空")
    @Size(max = 100, message = "参数键名不能超过100个字符")
    @Schema(description = "参数键名")
    private String configKey;

    @NotBlank(message = "参数键值不能为空")
    @Size(max = 500, message = "参数键值不能超过500个字符")
    @Schema(description = "参数值")
    private String configValue;

    @NotBlank(message = "系统内置标识不能为空")
    @Pattern(regexp = "^[01]$", message = "系统内置标识只能为0或1")
    @Schema(description = "是否系统内置：1是，0否")
    private String configType;

    @Schema(description = "备注")
    private String remark;
}
