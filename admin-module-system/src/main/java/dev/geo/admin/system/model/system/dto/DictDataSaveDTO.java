package dev.geo.admin.system.model.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 字典数据保存参数。
 */
@Getter
@Setter
public class DictDataSaveDTO {
    private Long id;
    private Long dictSort;

    @NotBlank(message = "字典标签不能为空")
    @Size(max = 100, message = "字典标签不能超过100个字符")
    private String dictLabel;

    @NotBlank(message = "字典键值不能为空")
    @Size(max = 100, message = "字典键值不能超过100个字符")
    private String dictValue;

    @NotBlank(message = "字典类型不能为空")
    @Size(max = 100, message = "字典类型不能超过100个字符")
    private String dictType;

    @Size(max = 100, message = "样式属性不能超过100个字符")
    private String cssClass;
    private String listClass;
    private String isDefault;
    private String status;
    private String remark;
}
