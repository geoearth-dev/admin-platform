package dev.geo.admin.generator.model.dto;

import dev.geo.admin.mybatis.model.page.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

/**
 * GenTable分页及导出查询参数。
 */
@Getter
@Setter
@Schema(description = "生成表查询条件")
public class GenTablePageReqDTO extends PageParam {
    /**
     * 表名称
     */
    @Schema(description = "数据库表名")
    private String tableName;

    /**
     * 表描述
     */
    @Schema(description = "表说明")
    private String tableComment;
    @Pattern(regexp = "createTime|updateTime")
    @Schema(description = "排序字段")
    private String orderByColumn;
    @Pattern(regexp = "ascending|descending")
    @Schema(description = "排序方向：asc升序，desc降序")
    private String isAsc;
}
