package dev.geo.admin.system.model.system.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 路由配置信息
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RouterVo {
    /**
     * 路由名字
     */
    private String name;

    /**
     * 路由地址
     */
    private String path;

    /**
     * 真实的重定向地址，不返回若依的 noRedirect 标记。
     */
    private String redirect;

    /**
     * 组件路径或标识，由前端转换为组件；纯目录可以为空。
     */
    private String component;

    /**
     * 其他元素
     */
    private MetaVo meta;

    /**
     * 子路由
     */
    private List<RouterVo> children;


}
