package dev.geo.admin.system.model.system.dto;

import dev.geo.admin.mybatis.model.page.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 字典类型分页查询参数。
 */
@Getter
@Setter
@Schema(description = "字典类型查询条件")
public class DictTypePageReqDTO extends PageParam {
    @Schema(description = "字典名称")
    private String dictName;
    @Schema(description = "字典类型标识")
    private String dictType;
    @Schema(description = "状态：1启用，0停用")
    private String status;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "开始时间")
    private Date beginTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "结束时间")
    private Date endTime;
}
