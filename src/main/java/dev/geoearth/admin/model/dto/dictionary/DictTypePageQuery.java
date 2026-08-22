package dev.geoearth.admin.model.dto.dictionary;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "字典类型分页查询参数")
public class DictTypePageQuery {
    @Schema(description = "字典名称，支持模糊查询", example = "用户性别")
    private String dictName;
    @Schema(description = "状态（0正常 1停用）", example = "0")
    private String status;
}
