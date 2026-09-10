package dev.geo.admin.system.model.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.Getter;
import lombok.Setter;

/**
 * 菜单保存参数。
 */
@Getter
@Setter
public class MenuSaveDTO {
    private Long id;

    @NotBlank(message = "菜单名称不能为空")
    @Size(max = 50, message = "菜单名称不能超过50个字符")
    private String menuName;

    @NotNull(message = "父菜单ID不能为空")
    private Long parentId = 0L;

    @NotNull(message = "显示顺序不能为空")
    private Integer order;

    @Size(max = 200, message = "路由地址不能超过200个字符")
    private String path;
    @Size(max = 255, message = "组件路径不能超过255个字符")
    private String component;
    @Size(max = 255, message = "路由参数不能超过255个字符")
    private String query;
    @Size(max = 50, message = "路由名称不能超过50个字符")
    private String routeName;
    @Size(max = 200, message = "外链地址不能超过200个字符")
    private String link;
    @Size(max = 200, message = "iframe地址不能超过200个字符")
    private String iframeSrc;
    /** 是否缓存：false 不缓存，true 缓存。 */
    @NotNull(message = "缓存设置不能为空")
    private Boolean keepAlive = false;

    @NotBlank(message = "菜单类型不能为空")
    private String menuType;

    /** 是否在菜单中隐藏：false 显示，true 隐藏。 */
    @NotNull(message = "菜单隐藏设置不能为空")
    private Boolean hideInMenu = false;

    @NotNull(message = "菜单状态不能为空")
    @Min(value = 0, message = "菜单状态只能为0或1")
    @Max(value = 1, message = "菜单状态只能为0或1")
    private Integer status = 1;

    @Size(max = 100, message = "权限标识不能超过100个字符")
    private String perms;

    private String icon;
}
