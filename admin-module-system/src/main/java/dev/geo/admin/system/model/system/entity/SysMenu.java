package dev.geo.admin.system.model.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import dev.geo.admin.mybatis.model.BaseEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单权限表 sys_menu
 */
@Setter
@Getter
@TableName("sys_menu")
public class SysMenu extends BaseEntity {

    /**
     * 菜单ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 菜单名称
     */
    @NotBlank(message = "菜单名称不能为空")
    @Size(min = 0, max = 50, message = "菜单名称长度不能超过50个字符")
    private String menuName;

    /**
     * 父菜单名称
     */
    @TableField(exist = false)
    private String parentName;

    /**
     * 父菜单ID
     */
    private Long parentId;

    /**
     * 显示顺序
     */
    @NotNull(message = "显示顺序不能为空")
    // order 是 SQL 关键字，MyBatis-Plus 生成增删改查 SQL 时需要转义。
    @TableField("`order`")
    private Integer order;

    /**
     * 路由地址
     */
    @Size(min = 0, max = 200, message = "路由地址不能超过200个字符")
    private String path;

    /**
     * 组件路径
     */
    @Size(min = 0, max = 255, message = "组件路径不能超过255个字符")
    private String component;

    /**
     * 路由参数
     */
    @TableField("`query`")
    private String query;

    /**
     * 路由名称，默认和路由地址相同的驼峰格式（注意：因为vue3版本的router会删除名称相同路由，为避免名字的冲突，特殊情况可以自定义）
     */
    private String routeName;

    /**
     * 外链地址，与 iframeSrc 互斥。
     */
    @Size(max = 200, message = "外链地址不能超过200个字符")
    private String link;

    /**
     * iframe 内嵌地址。
     */
    @Size(max = 200, message = "iframe地址不能超过200个字符")
    private String iframeSrc;

    /**
     * 是否缓存（0不缓存，1缓存）。
     */
    private Boolean keepAlive;

    /**
     * 类型（M目录 C菜单 F按钮）
     */
    @NotBlank(message = "菜单类型不能为空")
    private String menuType;

    /**
     * 是否在菜单中隐藏（0显示，1隐藏）。
     */
    private Boolean hideInMenu;

    /**
     * 菜单状态（0停用 1正常）
     */
    private Integer status;

    /**
     * 权限字符串
     */
    @Size(min = 0, max = 100, message = "权限标识长度不能超过100个字符")
    private String perms;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 子菜单
     */
    @TableField(exist = false)
    private List<SysMenu> children = new ArrayList<SysMenu>();


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
