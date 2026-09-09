package dev.geo.admin.system.model.system.dto;

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
public class ConfigSaveDTO {
    private Long id;

    @NotBlank(message = "参数名称不能为空")
    @Size(max = 100, message = "参数名称不能超过100个字符")
    private String configName;

    @NotBlank(message = "参数键名不能为空")
    @Size(max = 100, message = "参数键名不能超过100个字符")
    private String configKey;

    @NotBlank(message = "参数键值不能为空")
    @Size(max = 500, message = "参数键值不能超过500个字符")
    private String configValue;

    @NotBlank(message = "系统内置标识不能为空")
    @Pattern(regexp = "^[YN]$", message = "系统内置标识只能为Y或N")
    private String configType;

    private String remark;
}
