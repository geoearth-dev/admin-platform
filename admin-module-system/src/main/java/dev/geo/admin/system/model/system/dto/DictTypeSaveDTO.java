package dev.geo.admin.system.model.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 字典类型保存参数。
 */
@Getter
@Setter
public class DictTypeSaveDTO {
    private Long id;

    @NotBlank(message = "字典名称不能为空")
    @Size(max = 100, message = "字典名称不能超过100个字符")
    private String dictName;

    @NotBlank(message = "字典类型不能为空")
    @Size(max = 100, message = "字典类型不能超过100个字符")
    @Pattern(regexp = "^[a-z][a-z0-9_]*$", message = "字典类型必须以小写字母开头，且只能包含小写字母、数字和下划线")
    private String dictType;

    private String status;
    private String remark;
}
