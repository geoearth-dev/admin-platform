package dev.geo.admin.server.config;

import dev.geo.admin.common.config.AppConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.nio.file.Path;

/** 与 FileUploadVO.fileName 的 /profile/upload 路径对应。 */
@Configuration
public class UploadResourceConfig implements WebMvcConfigurer {
    private final Path uploadPath;

    // 依赖 AppConfig，确保配置绑定完成后再读取其静态路径。
    public UploadResourceConfig(AppConfig appConfig) {
        this.uploadPath = Path.of(AppConfig.getUploadPath()).toAbsolutePath();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = uploadPath.toUri().toString();
        registry.addResourceHandler("/profile/upload/**")
                .addResourceLocations(location.endsWith("/") ? location : location + "/");
    }
}
