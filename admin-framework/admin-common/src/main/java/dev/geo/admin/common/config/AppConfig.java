package dev.geo.admin.common.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

/**
 * 读取项目相关配置
 */
@Component
@ConfigurationProperties(prefix = "app")
public class AppConfig {
    /**
     * 项目名称
     */
    @Getter
    private static volatile String name;

    /**
     * 版本
     */
    @Getter
    private static volatile String version;

    /**
     * 版权年份
     */
    @Getter
    private static volatile String copyrightYear;

    /**
     * 作者
     */
    @Getter
    private static volatile String author;
    /**
     * 项目描述
     */
    @Getter
    private static volatile String description;

    /**
     * 上传路径
     */
    @Getter
    private static volatile String profile;
    /**
     * 获取地址开关
     */
    @Getter
    private static volatile boolean addressEnabled;

    /**
     * 验证码类型
     */
    @Getter
    private static volatile String captchaType;

    public void setName(String name) {
        AppConfig.name = name;
    }

    public void setVersion(String version) {
        AppConfig.version = version;
    }

    public void setCopyrightYear(String copyrightYear) {
        AppConfig.copyrightYear = copyrightYear;
    }

    public void setAuthor(String author) {
        AppConfig.author = author;
    }

    public void setDescription(String description) {
        AppConfig.description = description;
    }

    public void setProfile(String profile) {
        AppConfig.profile = Path.of(profile).normalize().toString();
    }

    public void setAddressEnabled(boolean addressEnabled) {
        AppConfig.addressEnabled = addressEnabled;
    }

    public void setCaptchaType(String captchaType) {
        AppConfig.captchaType = captchaType;
    }

    /**
     * 获取导入上传路径
     */
    public static String getImportPath() {
        return resolveProfilePath("import");
    }

    /**
     * 获取头像上传路径
     */
    public static String getAvatarPath() {
        return resolveProfilePath("avatar");
    }

    /**
     * 获取下载路径
     */
    public static String getDownloadPath() {
        return resolveProfilePath("download");
    }

    /**
     * 获取上传路径
     */
    public static String getUploadPath() {
        return resolveProfilePath("upload");
    }


    private static String resolveProfilePath(String directory) {
        if (profile == null || profile.isBlank()) {
            throw new IllegalStateException("app.profile 尚未配置或 AppConfig 尚未初始化");
        }

        return Path.of(profile).resolve(directory).normalize().toString();
    }
}
