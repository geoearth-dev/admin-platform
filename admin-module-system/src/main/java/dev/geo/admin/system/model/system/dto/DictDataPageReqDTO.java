package dev.geo.admin.system.model.system.dto;

import dev.geo.admin.mybatis.model.page.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 字典数据分页查询参数。
 */
@Getter
@Setter
@Schema(description = "字典数据查询条件")
public class DictDataPageReqDTO extends PageParam {
    @Schema(description = "字典类型标识")
    private String dictType;
    @Schema(description = "显示文本")
    private String dictLabel;
    @Schema(description = "状态：1启用，0停用")
    private String status;
}
