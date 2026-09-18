package dev.geo.admin.system.model.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

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
    @Min(0)
    private Long parentId = 0L;

    @NotNull(message = "显示顺序不能为空")
    @Min(value = 0, message = "排序不能小于0")
    private Integer order;

    @Size(max = 200, message = "路由地址不能超过200个字符")
    private String path;
    @Size(max = 255, message = "组件路径不能超过255个字符")
    private String component;
    /** 路由查询参数对象；null 表示清空。 */
    private Map<String, Object> query;
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
    @Pattern(regexp = "catalog|menu|embedded|link|button", message = "菜单类型不支持")
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

    @Size(max = 100)
    private String icon;
    /** 激活图标。 */
    @Size(max = 100, message = "激活图标长度不能超过100个字符")
    private String activeIcon;

    /** 激活菜单路径。 */
    @Size(max = 200, message = "激活菜单路径长度不能超过200个字符")
    private String activePath;

    /** 固定标签。 */
    private Boolean affixTab = false;

    /** 固定标签顺序。 */
    @Min(0)
    private Integer affixTabOrder = 0;

    /** 徽标内容。 */
    @Size(max = 50, message = "徽标内容长度不能超过50个字符")
    private String badge;

    /** 徽标类型 dot/normal。 */
    @Size(max = 10, message = "徽标类型 dot/normal长度不能超过10个字符")
    private String badgeType;

    /** 徽标样式。 */
    @Size(max = 20, message = "徽标样式长度不能超过20个字符")
    private String badgeVariants;

    /** 隐藏子菜单。 */
    private Boolean hideChildrenInMenu = false;

    /** 隐藏面包屑。 */
    private Boolean hideInBreadcrumb = false;

    /** 隐藏标签。 */
    private Boolean hideInTab = false;

    /** 内部重定向路径。 */
    @Size(max = 200, message = "内部重定向路径长度不能超过200个字符")
    private String redirect;

    /** 新窗口打开。 */
    private Boolean openInNewWindow = false;

    /** 不使用基础布局。 */
    private Boolean noBasicLayout = false;

    /** 同一路由最多标签数 -1不限。 */
    @Min(-1)
    private Integer maxNumOfOpenTab = -1;

}
