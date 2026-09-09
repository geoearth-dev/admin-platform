package dev.geo.admin.system.model.system.dto;

import dev.geo.admin.mybatis.model.page.PageParam;
import lombok.Getter;
import lombok.Setter;

/**
 * 字典数据分页查询参数。
 */
@Getter
@Setter
public class DictDataPageReqDTO extends PageParam {
    private String dictType;
    private String dictLabel;
    private String status;
}
