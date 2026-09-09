package dev.geo.admin.common.utils.file;

import cn.hutool.core.util.StrUtil;
import dev.geo.admin.common.config.AppConfig;
import dev.geo.admin.common.constant.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;

/**
 * 图片处理工具类
 */
public class ImageUtils {
    private static final Logger log = LoggerFactory.getLogger(ImageUtils.class);

    public static byte[] getImage(String imagePath) {
        try (InputStream inputStream = getFile(imagePath)) {
            return inputStream == null ? null : inputStream.readAllBytes();
        } catch (Exception e) {
            log.error("图片加载异常", e);
            return null;
        }
    }

    public static InputStream getFile(String imagePath) {
        try {
            byte[] result = readFile(imagePath);
            if (result != null) {
                result = Arrays.copyOf(result, result.length);
            }
            if (result != null) {
                return new ByteArrayInputStream(result);
            }
        } catch (Exception e) {
            log.error("获取图片异常 {}", e.getMessage());
        }
        return null;
    }

    /**
     * 读取文件为字节数据
     *
     * @param url 地址
     * @return 字节数据
     */
    public static byte[] readFile(String url) {
        try (InputStream inputStream = openInputStream(url)) {
            return inputStream.readAllBytes();
        } catch (Exception e) {
            log.error("获取文件路径异常", e);
            return null;
        }
    }

    private static InputStream openInputStream(String url) throws Exception {
            if (url.startsWith("http")) {
                // 网络地址
                URL urlObj = new URL(url);
                URLConnection urlConnection = urlObj.openConnection();
                urlConnection.setConnectTimeout(30 * 1000);
                urlConnection.setReadTimeout(60 * 1000);
                urlConnection.setDoInput(true);
                return urlConnection.getInputStream();
            } else {
                // 本机地址
                String localPath = AppConfig.getProfile();
                String downloadPath = localPath + StrUtil.subAfter(url, Constants.RESOURCE_PREFIX, false);
                return new FileInputStream(downloadPath);
            }
    }
}
