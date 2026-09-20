package dev.geo.admin.system.model.system.dto;

import dev.geo.admin.mybatis.model.page.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 用户分页查询参数。
 */
@Getter
@Setter
@Schema(description = "用户查询条件")
public class UserPageReqDTO extends PageParam {
    @Schema(description = "记录 ID")
    private Long id;
    @Schema(description = "所属部门 ID")
    private Long deptId;
    @Schema(description = "用户账号")
    private String userName;
    @Schema(description = "状态：1启用，0停用")
    private String status;
    @Schema(description = "手机号码")
    private String phoneNumber;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "开始时间")
    private Date startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "结束时间")
    private Date endTime;
}
