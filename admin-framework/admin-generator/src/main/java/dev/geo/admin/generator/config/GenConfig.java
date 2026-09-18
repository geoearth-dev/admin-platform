package dev.geo.admin.generator.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 读取代码生成相关配置
 */
@Component
@ConfigurationProperties(prefix = "gen")
public class GenConfig {
    /**
     * 作者
     */
    @Getter
    private static String author;

    /**
     * 生成包路径
     */
    @Getter
    private static String packageName;

    /**
     * 自动去除表前缀
     */
    @Getter
    private static boolean autoRemovePre;

    /**
     * 表前缀
     */
    @Getter
    private static String tablePrefix;

    /**
     * 是否允许生成文件覆盖到本地（自定义路径）
     */
    @Getter
    private static boolean allowOverwrite;

    public void setAuthor(String author) {
        GenConfig.author = author;
    }


    public void setPackageName(String packageName) {
        GenConfig.packageName = packageName;
    }


    public void setAutoRemovePre(boolean autoRemovePre) {
        GenConfig.autoRemovePre = autoRemovePre;
    }


    public void setTablePrefix(String tablePrefix) {
        GenConfig.tablePrefix = tablePrefix;
    }


    public void setAllowOverwrite(boolean allowOverwrite) {
        GenConfig.allowOverwrite = allowOverwrite;
    }
}
