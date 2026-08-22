package dev.geoearth.admin.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "用户分页查询参数")
public class UserPageQuery {
    @Size(max = 50, message = "用户名称不能超过50个字符")
    @Schema(description = "用户名称，支持模糊查询", example = "张三")
    private String name;

}