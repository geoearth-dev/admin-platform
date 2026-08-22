package dev.geoearth.admin.common.annotation;


import dev.geoearth.admin.common.enums.BusinessType;

import java.lang.annotation.*;

/**
 * 操作日志注解。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {
    /**
     * 操作标题。
     */
    String title() default "";

    /**
     * 业务操作类型。
     */
    BusinessType businessType() default BusinessType.OTHER;

    /**
     * 是否记录请求参数。
     */
    boolean saveRequestData() default true;

    /**
     * 是否记录响应数据。
     * <p>
     * 默认关闭，避免分页结果等大对象写入日志。
     */
    boolean saveResponseData() default false;

    /**
     * 额外排除的敏感字段。
     */
    String[] excludeParamNames() default {};
}
