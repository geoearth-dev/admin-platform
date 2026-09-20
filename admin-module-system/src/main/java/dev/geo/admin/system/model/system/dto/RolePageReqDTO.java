package dev.geo.admin.system.model.system.dto;

import dev.geo.admin.mybatis.model.page.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * 角色分页查询参数。
 */
@Getter
@Setter
@Schema(description = "角色查询条件")
public class RolePageReqDTO extends PageParam {
    @Schema(description = "记录 ID")
    private Long id;
    @Schema(description = "角色名称")
    private String roleName;
    @Schema(description = "角色标识，如 admin")
    private String roleKey;
    @Schema(description = "状态：1启用，0停用")
    private String status;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Schema(description = "开始时间")
    private LocalDate beginTime;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Schema(description = "结束时间")
    private LocalDate endTime;
}
