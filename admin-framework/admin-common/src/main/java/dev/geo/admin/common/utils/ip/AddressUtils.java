package dev.geo.admin.common.utils.ip;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import dev.geo.admin.common.config.AppConfig;
import dev.geo.admin.common.constant.Constants;
import dev.geo.admin.common.utils.http.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 获取地址类
 */
public class AddressUtils {
    private static final Logger log = LoggerFactory.getLogger(AddressUtils.class);

    // IP地址查询
    public static final String IP_URL = "https://whois.pconline.com.cn/ipJson.jsp";

    // 未知地址
    public static final String UNKNOWN = "未知";

    private static final int QUERY_TIMEOUT_MILLIS = 2000;

    public static String getRealAddressByIP(String ip) {
        if (IpUtils.isUnknown(ip)) {
            return UNKNOWN;
        }
        // 内网不查询
        if (IpUtils.internalIp(ip)) {
            return "内网IP";
        }
        if (AppConfig.isAddressEnabled()) {
            try {
                String rspStr = HttpUtils.sendGet(IP_URL, "ip=" + ip + "&json=true",
                        Constants.GBK, QUERY_TIMEOUT_MILLIS);
                if (StrUtil.isEmpty(rspStr)) {
                    log.error("获取地理位置异常 {}", ip);
                    return UNKNOWN;
                }
                JSONObject obj = JSON.parseObject(rspStr);
                return formatAddress(obj);
            } catch (Exception e) {
                log.error("获取地理位置异常 {}", e.getMessage());
            }
        }
        return UNKNOWN;
    }

    /**
     * 整理 IP 查询接口返回的地址。
     * 优先使用省、市；海外 IP 缺少省、市时使用完整地址字段。
     *
     * @param obj 地址查询结果
     * @return 整理后的地址，无有效地址时返回未知
     */
    static String formatAddress(JSONObject obj) {
        if (obj == null) {
            return UNKNOWN;
        }
        String region = normalizeAddress(obj.getString("pro"));
        String city = normalizeAddress(obj.getString("city"));
        if (!region.isEmpty() && !city.isEmpty()) {
            return region.equals(city) ? region : region + " " + city;
        }
        if (!region.isEmpty() || !city.isEmpty()) {
            return region + city;
        }
        String address = normalizeAddress(obj.getString("addr"));
        return address.isEmpty() ? UNKNOWN : address;
    }

    private static String normalizeAddress(String value) {
        String address = StrUtil.trimToEmpty(value);
        return StrUtil.equalsAnyIgnoreCase(address, "null", "unknown", "XX", "XX XX")
                ? "" : address;
    }
}
