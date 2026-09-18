package dev.geo.admin.mybatis.config;

import com.alibaba.druid.spring.boot4.autoconfigure.properties.DruidStatProperties;
import com.alibaba.druid.util.Utils;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * druid 配置
 */
@Configuration
public class DruidConfig {

    /**
     * 去除监控页面底部的广告
     */
    @Bean
    @ConditionalOnProperty(prefix = "spring.datasource.druid.stat-view-servlet", name = "enabled", havingValue = "true")
    public FilterRegistrationBean<Filter> removeDruidFilterRegistrationBean(DruidStatProperties properties) {
        // 获取web监控页面的参数
        DruidStatProperties.StatViewServlet config = properties.getStatViewServlet();
        // 提取common.js的配置路径
        String pattern = config.getUrlPattern() != null ? config.getUrlPattern() : "/druid/*";
        String commonJsPattern = pattern.replace("*", "js/common.js");
        String headerPattern = pattern.replace("*", "header.html");
        final String commonFilePath = "support/http/resources/js/common.js";
        final String headerFilePath = "support/http/resources/header.html";
        // 创建filter进行过滤
        Filter filter = new Filter() {
            @Override
            public void init(FilterConfig filterConfig) {
            }

            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
                chain.doFilter(request, response);
                HttpServletResponse httpResponse = (HttpServletResponse) response;
                if (httpResponse.isCommitted() || httpResponse.getStatus() != HttpServletResponse.SC_OK) {
                    return;
                }
                String requestUri = ((HttpServletRequest) request).getRequestURI();
                if (requestUri.endsWith("/js/common.js")) {
                    response.resetBuffer();
                    // 获取common.js
                    String text = Utils.readFromResource(commonFilePath);
                    // 正则替换banner, 除去底部的广告信息
                    text = text.replace("this.buildFooter();", "");
                    response.getWriter().write(text);
                }
                if (requestUri.endsWith("/header.html")) {
                    response.resetBuffer();
//                    response.setContentType("text/html;charset=UTF-8");
                    // 获取header.html
                    String text = Utils.readFromResource(headerFilePath);
                    // 正则替换Druid Monitor
                    text = text.replaceAll("<a[^>]*>\\s*Druid\\s+Monitor\\s*</a>", "");
                    response.getWriter().write(text);
                }
            }

            @Override
            public void destroy() {
            }
        };
        FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(filter);
        registrationBean.addUrlPatterns(commonJsPattern, headerPattern);
        return registrationBean;
    }
}
