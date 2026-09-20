package dev.geo.admin.system.model.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "菜单保存参数")
public class MenuSaveDTO {
    @Schema(description = "菜单 ID，新增时不传，修改时必传")
    private Long id;

    @NotBlank(message = "菜单名称不能为空")
    @Size(max = 50, message = "菜单名称不能超过50个字符")
    @Schema(description = "菜单名称")
    private String menuName;

    @NotNull(message = "父菜单ID不能为空")
    @Min(0)
    @Schema(description = "上级菜单 ID，0表示顶级菜单")
    private Long parentId = 0L;

    @NotNull(message = "显示顺序不能为空")
    @Min(value = 0, message = "排序不能小于0")
    @Schema(description = "显示顺序，越小越靠前")
    private Integer order;

    @Size(max = 200, message = "路由地址不能超过200个字符")
    @Schema(description = "前端路由路径")
    private String path;
    @Size(max = 255, message = "组件路径不能超过255个字符")
    @Schema(description = "页面组件路径，相对于 src/views")
    private String component;
    /** 路由查询参数对象；null 表示清空。 */
    @Schema(description = "路由查询参数对象")
    private Map<String, Object> query;
    @Size(max = 50, message = "路由名称不能超过50个字符")
    @Schema(description = "路由名称，需保持唯一")
    private String routeName;
    @Size(max = 200, message = "外链地址不能超过200个字符")
    @Schema(description = "外链地址")
    private String link;
    @Size(max = 200, message = "iframe地址不能超过200个字符")
    @Schema(description = "内嵌页面地址")
    private String iframeSrc;
    /** 是否缓存：false 不缓存，true 缓存。 */
    @NotNull(message = "缓存设置不能为空")
    @Schema(description = "是否缓存页面，适用于普通页面和内嵌页面")
    private Boolean keepAlive = false;

    @NotBlank(message = "菜单类型不能为空")
    @Pattern(regexp = "catalog|menu|embedded|link|button", message = "菜单类型不支持")
    @Schema(description = "菜单类型：catalog目录，menu页面，embedded内嵌，link外链，button按钮")
    private String menuType;

    /** 是否在菜单中隐藏：false 显示，true 隐藏。 */
    @NotNull(message = "菜单隐藏设置不能为空")
    @Schema(description = "是否隐藏菜单入口")
    private Boolean hideInMenu = false;

    @NotNull(message = "菜单状态不能为空")
    @Min(value = 0, message = "菜单状态只能为0或1")
    @Max(value = 1, message = "菜单状态只能为0或1")
    @Schema(description = "状态：1启用，0停用")
    private Integer status = 1;

    @Size(max = 100, message = "权限标识不能超过100个字符")
    @Schema(description = "权限标识，如 system:user:list")
    private String perms;

    @Size(max = 100)
    @Schema(description = "菜单图标")
    private String icon;
    /** 激活图标。 */
    @Size(max = 100, message = "激活图标长度不能超过100个字符")
    @Schema(description = "选中时的图标")
    private String activeIcon;

    /** 激活菜单路径。 */
    @Size(max = 200, message = "激活菜单路径长度不能超过200个字符")
    @Schema(description = "需要高亮的菜单路径")
    private String activePath;

    /** 固定标签。 */
    @Schema(description = "是否固定标签页")
    private Boolean affixTab = false;

    /** 固定标签顺序。 */
    @Min(0)
    @Schema(description = "固定标签的排列顺序")
    private Integer affixTabOrder = 0;

    /** 徽标内容。 */
    @Size(max = 50, message = "徽标内容长度不能超过50个字符")
    @Schema(description = "徽标文字")
    private String badge;

    /** 徽标类型 dot/normal。 */
    @Size(max = 10, message = "徽标类型 dot/normal长度不能超过10个字符")
    @Schema(description = "徽标类型：dot圆点，normal文字")
    private String badgeType;

    /** 徽标样式。 */
    @Size(max = 20, message = "徽标样式长度不能超过20个字符")
    @Schema(description = "徽标样式：default、destructive、primary、success、warning")
    private String badgeVariants;

    /** 隐藏子菜单。 */
    @Schema(description = "是否隐藏子菜单")
    private Boolean hideChildrenInMenu = false;

    /** 隐藏面包屑。 */
    @Schema(description = "是否隐藏面包屑")
    private Boolean hideInBreadcrumb = false;

    /** 隐藏标签。 */
    @Schema(description = "是否隐藏标签页")
    private Boolean hideInTab = false;

    /** 内部重定向路径。 */
    @Size(max = 200, message = "内部重定向路径长度不能超过200个字符")
    @Schema(description = "进入路由后的跳转路径")
    private String redirect;

    /** 新窗口打开。 */
    @Schema(description = "是否在新窗口打开")
    private Boolean openInNewWindow = false;

    /** 不使用基础布局。 */
    @Schema(description = "是否隐藏基础布局")
    private Boolean noBasicLayout = false;

    /** 同一路由最多标签数 -1不限。 */
    @Min(-1)
    @Schema(description = "同一路由最多打开的标签数，-1表示不限")
    private Integer maxNumOfOpenTab = -1;

}
