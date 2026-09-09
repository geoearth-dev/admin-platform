package dev.geo.admin.system.model.system.vo;

import dev.geo.admin.common.constant.DateTimeFormat;
import dev.geo.admin.excel.annotation.Excel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;


/**
 * 系统配置 Response VO 对象 system_content
 */
@Schema(description = "系统配置 Response VO")
@Data
@Builder
public class SysContentRespVO {


    @Excel(name = "ID")
    @Schema(description = "ID")
    private Long id;

    @Excel(name = "系统名称")
    @Schema(description = "系统名称", example = "")
    private String sysName;

    @Excel(name = "loginLogo")
    @Schema(description = "loginLogo", example = "")
    private String loginLogo;

    @Excel(name = "logo")
    @Schema(description = "logo", example = "")
    private String logo;

    @Excel(name = "轮播图")
    @Schema(description = "轮播图", example = "")
    private String carouselImage;

    @Excel(name = "联系电话")
    @Schema(description = "联系电话", example = "")
    private String contactNumber;

    @Excel(name = "电子邮箱")
    @Schema(description = "电子邮箱", example = "")
    private String email;

    @Excel(name = "版权方")
    @Schema(description = "版权方", example = "")
    private String copyright;

    @Excel(name = "备案号")
    @Schema(description = "备案号", example = "")
    private String recordNumber;

    @Excel(name = "删除标记")
    @Schema(description = "删除标记", example = "")
    private Boolean delFlag;

    @Excel(name = "状态")
    @Schema(description = "状态", example = "")
    private Integer status;

    @Excel(name = "创建人")
    @Schema(description = "创建人", example = "")
    private String createBy;

    @Excel(name = "创建人id")
    @Schema(description = "创建人id", example = "")
    private Long creatorId;

    @Excel(name = "创建时间", width = 30, dateFormat = DateTimeFormat.DATE_TIME_PATTERN)
    @Schema(description = "创建时间", example = "")
    private Instant createTime;

    @Excel(name = "修改人")
    @Schema(description = "修改人", example = "")
    private String updateBy;

    @Excel(name = "修改人id")
    @Schema(description = "修改人id", example = "")
    private Long updaterId;

    @Excel(name = "修改时间", width = 30, dateFormat = DateTimeFormat.DATE_TIME_PATTERN)
    @Schema(description = "修改时间", example = "")
    private Instant updateTime;

    @Excel(name = "备注")
    @Schema(description = "备注", example = "")
    private String remark;

}
