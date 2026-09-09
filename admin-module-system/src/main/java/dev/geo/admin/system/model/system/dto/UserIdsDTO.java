package dev.geo.admin.system.model.system.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户主键集合参数。
 */
@Getter
@Setter
public class UserIdsDTO {
    @NotNull(message = "用户主键集合不能为空")
    private Long[] userIds;
}
