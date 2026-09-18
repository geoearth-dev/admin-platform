package dev.geo.admin.system.model.system.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 路由的菜单与页面配置。
 */
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MetaVo {
    /**
     * 设置该路由在侧边栏和面包屑中展示的名字
     */
    private String title;

    /**
     * 设置该路由的图标，对应路径src/assets/icons/svg
     */
    private String icon;

    /**
     * 是否启用页面缓存。
     */
    private boolean keepAlive;

    /** 是否隐藏菜单入口，不影响路由注册。 */
    private boolean hideInMenu;

    /** 同级菜单排序号。 */
    private Integer order;

    /** 点击菜单时携带的固定查询参数。 */
    private Map<String, Object> query;

    /**
     * 外部跳转地址。
     */
    private String link;

    /** iframe 内嵌地址。 */
    private String iframeSrc;

    /** 激活图标。 */
    private String activeIcon;

    /** 激活菜单路径。 */
    private String activePath;

    /** 固定标签。 */
    private Boolean affixTab;

    /** 固定标签顺序。 */
    private Integer affixTabOrder;

    /** 徽标内容。 */
    private String badge;

    /** 徽标类型 dot/normal。 */
    private String badgeType;

    /** 徽标样式。 */
    private String badgeVariants;

    /** 隐藏子菜单。 */
    private Boolean hideChildrenInMenu;

    /** 隐藏面包屑。 */
    private Boolean hideInBreadcrumb;

    /** 隐藏标签。 */
    private Boolean hideInTab;

    /** 新窗口打开。 */
    private Boolean openInNewWindow;

    /** 不使用基础布局。 */
    private Boolean noBasicLayout;

    /** 同一路由最多标签数 -1不限。 */
    private Integer maxNumOfOpenTab;

}
