package dev.geo.admin.common.filter;

import com.alibaba.fastjson2.filter.SimplePropertyPreFilter;

/**
 * 排除JSON敏感属性
 */
public class PropertyPreExcludeFilter extends SimplePropertyPreFilter {

    public PropertyPreExcludeFilter addExcludes(String... filters) {
        for (String filter : filters) {
            this.getExcludes().add(filter);
        }
        return this;
    }
}
