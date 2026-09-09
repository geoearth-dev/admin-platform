package dev.geo.admin.system.model.system.dto;

import dev.geo.admin.mybatis.model.page.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统展示配置分页查询条件。
 */
@EqualsAndHashCode(callSuper = true)
@Schema(description = "系统展示配置分页查询条件")
@Data
public class SysContentPageReqDTO extends PageParam {

    @Schema(description = "配置ID", example = "1")
    private Long id;

    @Schema(description = "系统名称", example = "GeoEarth 管理平台")
    private String sysName;

    @Schema(description = "状态", example = "0")
    private Integer status;
}
