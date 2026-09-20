package dev.geo.admin.system.model.system.dto;

import dev.geo.admin.mybatis.model.page.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 岗位分页查询参数。
 */
@Getter
@Setter
@Schema(description = "岗位查询条件")
public class PostPageReqDTO extends PageParam {
    @Schema(description = "岗位编码")
    private String postCode;
    @Schema(description = "岗位名称")
    private String postName;
    @Schema(description = "状态：1启用，0停用")
    private String status;
}
