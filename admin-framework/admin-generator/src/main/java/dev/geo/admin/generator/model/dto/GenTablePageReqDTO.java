package dev.geo.admin.generator.model.dto;

import dev.geo.admin.mybatis.model.page.PageParam;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

/**
 * GenTable分页及导出查询参数。
 */
@Getter
@Setter
public class GenTablePageReqDTO extends PageParam {
    /**
     * 表名称
     */
    private String tableName;

    /**
     * 表描述
     */
    private String tableComment;
}
