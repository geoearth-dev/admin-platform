package dev.geo.admin.system.model.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

/**
 * 字典数据保存参数。
 */
@Getter
@Setter
@Schema(description = "字典数据保存参数")
public class DictDataSaveDTO {
    @Schema(description = "记录 ID")
    private Long id;
    @Schema(description = "显示顺序，越小越靠前")
    private Long dictSort;

    @NotBlank(message = "字典标签不能为空")
    @Size(max = 100, message = "字典标签不能超过100个字符")
    @Schema(description = "显示文本")
    private String dictLabel;

    @NotBlank(message = "字典键值不能为空")
    @Size(max = 100, message = "字典键值不能超过100个字符")
    @Schema(description = "字典值")
    private String dictValue;

    @NotBlank(message = "字典类型不能为空")
    @Size(max = 100, message = "字典类型不能超过100个字符")
    @Schema(description = "字典类型标识")
    private String dictType;

    @Size(max = 100, message = "样式属性不能超过100个字符")
    @Schema(description = "自定义样式类名")
    private String cssClass;
    @Schema(description = "标签样式")
    private String listClass;
    @Schema(description = "是否默认选项：1是，0否")
    @Pattern(regexp = "^[01]$", message = "默认标识只能为0或1")
    private String isDefault;
    @Schema(description = "状态：1启用，0停用")
    private String status;
    @Schema(description = "备注")
    private String remark;
}
