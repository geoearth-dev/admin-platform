package dev.geo.admin.system.model.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 登录页等公开页面使用的系统展示配置。
 */
@Getter
@Setter
@Schema(description = "公开的系统展示配置")
public class SysContentPublicVO {

    @Schema(description = "配置ID")
    private Long id;

    @Schema(description = "系统名称")
    private String sysName;

    @Schema(description = "登录页Logo地址")
    private String loginLogo;

    @Schema(description = "系统Logo地址")
    private String logo;

    @Schema(description = "轮播图地址")
    private String carouselImage;

    @Schema(description = "联系电话")
    private String contactNumber;

    @Schema(description = "电子邮箱")
    private String email;

    @Schema(description = "版权信息")
    private String copyright;

    @Schema(description = "备案号")
    private String recordNumber;

    @Schema(description = "状态")
    private Integer status;
}
