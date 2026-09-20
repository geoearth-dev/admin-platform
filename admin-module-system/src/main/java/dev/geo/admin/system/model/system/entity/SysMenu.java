package dev.geo.admin.system.model.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import dev.geo.admin.mybatis.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 菜单权限表 sys_menu
 */
@Setter
@Getter
@TableName(value = "sys_menu", autoResultMap = true)
@Schema(description = "菜单")
public class SysMenu extends BaseEntity {

    /**
     * 菜单ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "菜单 ID")
    private Long id;

    /**
     * 菜单名称
     */
    @NotBlank(message = "菜单名称不能为空")
    @Size(min = 0, max = 50, message = "菜单名称长度不能超过50个字符")
    @Schema(description = "菜单名称")
    private String menuName;

    /**
     * 父菜单名称
     */
    @TableField(exist = false)
    @Schema(description = "上级名称")
    private String parentName;

    /**
     * 父菜单ID
     */
    @Schema(description = "上级 ID，0表示根节点")
    private Long parentId;

    /**
     * 显示顺序
     */
    @NotNull(message = "显示顺序不能为空")
    // order 是 SQL 关键字，MyBatis-Plus 生成增删改查 SQL 时需要转义。
    @TableField("`order`")
    @Schema(description = "显示顺序，越小越靠前")
    private Integer order;

    /**
     * 路由地址
     */
    @Size(min = 0, max = 200, message = "路由地址不能超过200个字符")
    @Schema(description = "前端路由路径")
    private String path;

    /**
     * 组件路径
     */
    @Size(min = 0, max = 255, message = "组件路径不能超过255个字符")
    @Schema(description = "页面组件路径，相对于 src/views")
    private String component;

    /**
     * 路由查询参数对象，由 JSON 类型处理器负责数据库读写。
     */
    @TableField(value = "`query`", typeHandler = Fastjson2TypeHandler.class)
    @Schema(description = "路由查询参数对象")
    private Map<String, Object> query;

    /**
     * 路由名称，默认和路由地址相同的驼峰格式（注意：因为vue3版本的router会删除名称相同路由，为避免名字的冲突，特殊情况可以自定义）
     */
    @Schema(description = "路由名称，需保持唯一")
    private String routeName;

    /**
     * 外链地址，与 iframeSrc 互斥。
     */
    @Size(max = 200, message = "外链地址不能超过200个字符")
    @Schema(description = "外链地址")
    private String link;

    /**
     * iframe 内嵌地址。
     */
    @Size(max = 200, message = "iframe地址不能超过200个字符")
    @Schema(description = "内嵌页面地址")
    private String iframeSrc;

    /**
     * 是否缓存（0不缓存，1缓存）。
     */
    @Schema(description = "是否缓存页面")
    private Boolean keepAlive;

    /**
     * 类型（catalog目录 menu菜单 embedded内嵌 link外链 button按钮）
     */
    @NotBlank(message = "菜单类型不能为空")
    @Schema(description = "菜单类型：catalog目录，menu页面，embedded内嵌，link外链，button按钮")
    private String menuType;

    /**
     * 是否在菜单中隐藏（0显示，1隐藏）。
     */
    @Schema(description = "是否隐藏菜单入口")
    private Boolean hideInMenu;

    /**
     * 菜单状态（0停用 1正常）
     */
    @Schema(description = "状态：1启用，0停用")
    private Integer status;

    /**
     * 权限字符串
     */
    @Size(min = 0, max = 100, message = "权限标识长度不能超过100个字符")
    @Schema(description = "权限标识，如 system:user:list")
    private String perms;

    /**
     * 菜单图标
     */
    @Schema(description = "菜单图标")
    private String icon;

    /**
     * 子菜单
     */
    @TableField(exist = false)
    @Schema(description = "子节点")
    private List<SysMenu> children = new ArrayList<SysMenu>();


    /** 激活图标。 */
    @Schema(description = "选中时的图标")
    private String activeIcon;

    /** 激活菜单路径。 */
    @Schema(description = "需要高亮的菜单路径")
    private String activePath;

    /** 固定标签。 */
    @Schema(description = "是否固定标签页")
    private Boolean affixTab;

    /** 固定标签顺序。 */
    @Schema(description = "固定标签的排列顺序")
    private Integer affixTabOrder;

    /** 徽标内容。 */
    @Schema(description = "徽标文字")
    private String badge;

    /** 徽标类型 dot/normal。 */
    @Schema(description = "徽标类型：dot圆点，normal文字")
    private String badgeType;

    /** 徽标样式。 */
    @Schema(description = "徽标样式：default、destructive、primary、success、warning")
    private String badgeVariants;

    /** 隐藏子菜单。 */
    @Schema(description = "是否隐藏子菜单")
    private Boolean hideChildrenInMenu;

    /** 隐藏面包屑。 */
    @Schema(description = "是否隐藏面包屑")
    private Boolean hideInBreadcrumb;

    /** 隐藏标签。 */
    @Schema(description = "是否隐藏标签页")
    private Boolean hideInTab;

    /** 内部重定向路径。 */
    @Schema(description = "进入路由后的跳转路径")
    private String redirect;

    /** 新窗口打开。 */
    @Schema(description = "是否在新窗口打开")
    private Boolean openInNewWindow;

    /** 不使用基础布局。 */
    @Schema(description = "是否隐藏基础布局")
    private Boolean noBasicLayout;

    /** 同一路由最多标签数 -1不限。 */
    @Schema(description = "同一路由最多打开的标签数，-1表示不限")
    private Integer maxNumOfOpenTab;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("menuName", getMenuName())
                .append("parentId", getParentId())
                .append("order", getOrder())
                .append("path", getPath())
                .append("component", getComponent())
                .append("query", getQuery())
                .append("routeName", getRouteName())
                .append("link", getLink())
                .append("iframeSrc", getIframeSrc())
                .append("keepAlive", getKeepAlive())
                .append("menuType", getMenuType())
                .append("hideInMenu", getHideInMenu())
                .append("status ", getStatus())
                .append("perms", getPerms())
                .append("icon", getIcon())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .toString();
    }
}
