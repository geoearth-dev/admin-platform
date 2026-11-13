package dev.geo.admin.common.utils.http;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import dev.geo.admin.common.constant.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * HTTP 请求工具类。
 */
public final class HttpUtils {
    private static final Logger log = LoggerFactory.getLogger(HttpUtils.class);

    /**
     * 默认连接超时和读取超时时间，单位：毫秒。
     */
    private static final int DEFAULT_TIMEOUT_MILLIS = 5000;

    private HttpUtils() {
    }

    /**
     * 发送 GET 请求，使用 UTF-8 读取响应正文。
     *
     * @param url 请求地址
     * @return 响应正文，请求失败时返回空字符串
     */
    public static String sendGet(String url) {
        return sendGet(url, StrUtil.EMPTY);
    }

    /**
     * 发送带查询参数的 GET 请求，使用 UTF-8 读取响应正文。
     *
     * @param url 请求地址
     * @param param 已编码的查询参数，格式为 name1=value1&name2=value2
     * @return 响应正文，请求失败时返回空字符串
     */
    public static String sendGet(String url, String param) {
        return sendGet(url, param, Constants.UTF8);
    }

    /**
     * 发送 GET 请求，使用指定字符集读取响应正文。
     *
     * @param url 请求地址
     * @param param 已编码的查询参数，格式为 name1=value1&name2=value2
     * @param charsetName 响应字符集，例如 UTF-8、GBK
     * @return 响应正文，请求失败时返回空字符串
     */
    public static String sendGet(String url, String param, String charsetName) {
        return sendGet(url, param, charsetName, DEFAULT_TIMEOUT_MILLIS);
    }

    /**
     * 发送 GET 请求，并指定响应字符集和超时时间。
     *
     * @param url 请求地址，可以包含已有的查询参数
     * @param param 已编码的查询参数，格式为 name1=value1&name2=value2
     * @param charsetName 响应字符集，例如 UTF-8、GBK
     * @param timeoutMillis 连接和读取超时时间，单位：毫秒，必须大于零
     * @return 响应正文，请求失败时返回空字符串
     * @throws IllegalArgumentException 超时时间小于或等于零
     */
    public static String sendGet(String url, String param, String charsetName, int timeoutMillis) {
        return execute("GET", url, param, null, charsetName, timeoutMillis);
    }

    /**
     * 发送表单 POST 请求。
     *
     * @param url 请求地址
     * @param param 已编码的表单正文，格式为 name1=value1&name2=value2
     * @return 响应正文，请求失败时返回空字符串
     */
    public static String sendPost(String url, String param) {
        return sendPost(url, param, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
    }

    /**
     * 发送指定内容类型的 POST 请求。
     *
     * @param url 请求地址
     * @param param 请求正文
     * @param contentType 正文类型，可以包含 charset；未指定字符集时使用 UTF-8
     * @return 响应正文，请求失败时返回空字符串
     */
    public static String sendPost(String url, String param, String contentType) {
        return sendPost(url, param, contentType, DEFAULT_TIMEOUT_MILLIS);
    }

    /**
     * 发送 POST 请求，并指定内容类型和超时时间。
     *
     * @param url 请求地址
     * @param param 请求正文
     * @param contentType 正文类型，可以包含 charset；未指定字符集时使用 UTF-8
     * @param timeoutMillis 连接和读取超时时间，单位：毫秒，必须大于零
     * @return 响应正文，请求失败时返回空字符串
     * @throws IllegalArgumentException 超时时间小于或等于零
     */
    public static String sendPost(String url, String param, String contentType, int timeoutMillis) {
        return execute("POST", url, param, contentType, null, timeoutMillis);
    }

    /**
     * 发送 POST 请求，保留原有 HTTPS 请求方法名。
     * HTTPS 连接使用系统默认的证书和主机名校验。
     *
     * @param url 请求地址
     * @param param 已编码的表单正文
     * @return 响应正文，请求失败时返回空字符串
     */
    public static String sendSSLPost(String url, String param) {
        return sendPost(url, param);
    }

    /**
     * 发送指定内容类型的 POST 请求，保留原有 HTTPS 请求方法名。
     *
     * @param url 请求地址
     * @param param 请求正文
     * @param contentType 正文类型，可以包含 charset
     * @return 响应正文，请求失败时返回空字符串
     */
    public static String sendSSLPost(String url, String param, String contentType) {
        return sendPost(url, param, contentType);
    }

    /**
     * 统一执行 GET、POST 请求，并关闭请求所使用的连接和流。
     *
     * @param method 请求方法
     * @param url 请求地址
     * @param param GET 查询参数或 POST 请求正文
     * @param contentType POST 正文类型
     * @param responseCharsetName 响应字符集，为 null 时从响应头读取
     * @param timeoutMillis 连接和读取超时时间，单位：毫秒
     * @return 响应正文，请求失败时返回空字符串
     */
    private static String execute(String method, String url, String param, String contentType,
                                  String responseCharsetName, int timeoutMillis) {
        if (timeoutMillis <= 0) {
            throw new IllegalArgumentException("HTTP 超时时间必须大于零。");
        }
        HttpURLConnection connection = null;
        try {
            Charset responseCharset = responseCharsetName == null ? null : Charset.forName(responseCharsetName);
            String requestUrl = "GET".equals(method) ? appendQuery(url, param) : url;
            connection = (HttpURLConnection) URI.create(requestUrl).toURL().openConnection();
            connection.setRequestMethod(method);
            connection.setConnectTimeout(timeoutMillis);
            connection.setReadTimeout(timeoutMillis);
            connection.setRequestProperty("Accept", "*/*");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            if ("POST".equals(method)) {
                MediaType type = MediaType.parseMediaType(StrUtil.blankToDefault(
                        contentType, MediaType.APPLICATION_FORM_URLENCODED_VALUE));
                Charset requestCharset = type.getCharset() == null ? StandardCharsets.UTF_8 : type.getCharset();
                connection.setRequestProperty("Content-Type", new MediaType(type, requestCharset).toString());
                connection.setDoOutput(true);
                byte[] body = StrUtil.nullToEmpty(param).getBytes(requestCharset);
                connection.setFixedLengthStreamingMode(body.length);
                try (OutputStream out = connection.getOutputStream()) {
                    out.write(body);
                }
            }

            int status = connection.getResponseCode();
            if (status < 200 || status >= 300) {
                log.warn("HTTP {} 请求失败，地址: {}, 状态码: {}", method, endpoint(url), status);
                return StrUtil.EMPTY;
            }
            if (responseCharset == null) {
                responseCharset = responseCharset(connection.getContentType());
            }
            try (InputStream in = connection.getInputStream()) {
                return new String(in.readAllBytes(), responseCharset);
            }
        } catch (Exception e) {
            // 不输出查询参数、正文或异常中的完整 URL，避免把凭据写入日志。
            log.warn("HTTP {} 请求失败，地址: {}, 异常类型: {}", method, endpoint(url), e.getClass().getSimpleName());
            return StrUtil.EMPTY;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * 将查询参数追加到地址中，并保留原有查询参数和片段标识。
     *
     * @param url 请求地址
     * @param param 需要追加的查询参数
     * @return 拼接后的请求地址
     */
    private static String appendQuery(String url, String param) {
        if (StrUtil.isBlank(param)) {
            return url;
        }
        int fragmentIndex = url.indexOf('#');
        String base = fragmentIndex < 0 ? url : url.substring(0, fragmentIndex);
        String fragment = fragmentIndex < 0 ? "" : url.substring(fragmentIndex);
        String separator = base.contains("?") ? (base.endsWith("?") || base.endsWith("&") ? "" : "&") : "?";
        return base + separator + param + fragment;
    }

    /**
     * 从 Content-Type 响应头读取字符集，缺失或无效时使用 UTF-8。
     *
     * @param contentType Content-Type 响应头
     * @return 响应字符集
     */
    private static Charset responseCharset(String contentType) {
        if (StrUtil.isNotBlank(contentType)) {
            try {
                Charset charset = MediaType.parseMediaType(contentType).getCharset();
                if (charset != null) {
                    return charset;
                }
            } catch (IllegalArgumentException ignored) {
                // 响应未提供有效编码时使用 UTF-8。
            }
        }
        return StandardCharsets.UTF_8;
    }

    /**
     * 提取用于日志记录的请求地址，不包含用户信息、查询参数和片段标识。
     *
     * @param url 原始请求地址
     * @return 不含敏感参数的地址，解析失败时返回“无效地址”
     */
    private static String endpoint(String url) {
        try {
            URI uri = URI.create(url);
            return new URI(uri.getScheme(), null, uri.getHost(), uri.getPort(), uri.getPath(), null, null).toString();
        } catch (Exception ignored) {
            return "无效地址";
        }
    }

    /**
     * 判断地址是否使用 HTTP 或 HTTPS 协议。
     *
     * @param url 待检查的地址
     * @return 使用 HTTP 或 HTTPS 协议时返回 true
     */
    public static boolean isHttpUrl(String url) {
        return HttpUtil.isHttp(url) || HttpUtil.isHttps(url);
    }
}
