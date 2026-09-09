package dev.geo.admin.security.config.properties;

import dev.geo.admin.common.annotation.Anonymous;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PathPatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 设置Anonymous注解允许匿名访问的url
 */
@Component
public class PermitAllUrlProperties implements InitializingBean {
    /**
     * PathVariable 匹配规则，例如：
     * /user/{id} -> /user/*
     */
    private static final Pattern PATH_VARIABLE_PATTERN = Pattern.compile("\\{[^}]+}");
    private static final String ASTERISK = "*";

    private final RequestMappingHandlerMapping handlerMapping;

    private final Set<String> urls = new LinkedHashSet<>();

    public PermitAllUrlProperties(@Qualifier("requestMappingHandlerMapping") RequestMappingHandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
    }


    @Override
    public void afterPropertiesSet() {
        handlerMapping.getHandlerMethods().forEach(
                (mappingInfo, handlerMethod) -> {
                    // 方法或 Controller 类上存在 @Anonymous
                    if (!isAnonymous(handlerMethod)) {
                        return;
                    }
                    PathPatternsRequestCondition condition = mappingInfo.getPathPatternsCondition();
                    if (condition == null) {
                        return;
                    }
                    condition.getPatternValues().stream()
                            //  /system/user/{id} -> /system/user/*
                            .map(path -> PATH_VARIABLE_PATTERN.matcher(path).replaceAll(ASTERISK))
                            .forEach(urls::add);
                }
        );
    }

    /**
     * 判断方法或 Controller 是否允许匿名访问。
     */
    private boolean isAnonymous(HandlerMethod handlerMethod) {
        return handlerMethod.hasMethodAnnotation(Anonymous.class) ||
                AnnotatedElementUtils.hasAnnotation(handlerMethod.getBeanType(), Anonymous.class);
    }

    public Set<String> getUrls() {
        return Collections.unmodifiableSet(urls);
    }

}