package dev.geo.admin.system.model.system.dto;

import dev.geo.admin.mybatis.model.page.PageParam;
import lombok.Getter;
import lombok.Setter;

/**
 * 岗位分页查询参数。
 */
@Getter
@Setter
public class PostPageReqDTO extends PageParam {
    private String postCode;
    private String postName;
    private String status;
}
