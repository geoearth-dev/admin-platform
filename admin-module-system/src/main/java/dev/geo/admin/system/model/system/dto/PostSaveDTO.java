package dev.geo.admin.system.model.system.dto;

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
public class PostSaveDTO {
    private Long id;

    @NotBlank(message = "岗位编码不能为空")
    @Size(max = 64, message = "岗位编码不能超过64个字符")
    private String postCode;

    @NotBlank(message = "岗位名称不能为空")
    @Size(max = 50, message = "岗位名称不能超过50个字符")
    private String postName;

    @NotNull(message = "显示顺序不能为空")
    private Integer postSort;

    private String status;
    private String remark;
}
