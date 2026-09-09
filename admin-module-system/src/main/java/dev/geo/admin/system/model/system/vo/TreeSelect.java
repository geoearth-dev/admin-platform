package dev.geo.admin.system.model.system.vo;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import dev.geo.admin.common.constant.UserConstants;
import dev.geo.admin.system.model.system.entity.SysDept;
import dev.geo.admin.system.model.system.entity.SysMenu;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TreeSelect树结构实体类
 */
@Getter
@Setter
public class TreeSelect {

    /**
     * 节点ID
     */
    private Long id;

    /**
     * 节点名称
     */
    private String label;

    /**
     * 节点禁用
     */
    private boolean disabled = false;

    /**
     * 子节点
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<TreeSelect> children;


    public TreeSelect(SysDept dept) {
        this.id = dept.getId();
        this.label = dept.getDeptName();
        this.disabled = StrUtil.equals(UserConstants.DEPT_DISABLE, dept.getStatus());
        this.children = dept.getChildren().stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    public TreeSelect(SysMenu menu) {
        this.id = menu.getId();
        this.label = menu.getMenuName();
        this.children = menu.getChildren().stream().map(TreeSelect::new).collect(Collectors.toList());
    }

}
