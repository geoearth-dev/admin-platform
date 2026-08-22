package dev.geoearth.admin.model.dto.dictionary;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "字典数据分页查询参数")
public class DictDataPageQuery {
    @Schema(description = "字典类型名称，支持模糊查询", example = "用户性别")
    private String dictName;
    @Schema(description = "字典类型", example = "sys_user_sex")
    private String dictType;
    @Schema(description = "字典标签名称，支持模糊查询", example = "男")
    private String dictLabel;
    @Schema(description = "状态（0正常 1停用）", example = "0")
    private String status;
}
