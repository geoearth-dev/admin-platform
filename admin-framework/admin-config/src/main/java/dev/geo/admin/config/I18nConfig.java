package dev.geo.admin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Locale;

/**
 * 资源文件配置加载
 */
@Configuration(proxyBeanMethods = false)
public class I18nConfig {
    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        // 默认语言
        resolver.setDefaultLocale(Locale.SIMPLIFIED_CHINESE);

        return resolver;
    }

}
