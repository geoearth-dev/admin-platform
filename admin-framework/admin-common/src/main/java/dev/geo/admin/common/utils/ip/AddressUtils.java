package dev.geo.admin.common.utils.ip;

import cn.hutool.core.util.StrUtil;
import dev.geo.admin.common.config.AppConfig;
import org.lionsoul.ip2region.service.Config;
import org.lionsoul.ip2region.service.Ip2Region;
import org.lionsoul.ip2region.xdb.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.net.InetAddress;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * IP 归属地查询工具。
 *
 * https://github.com/lionsoul2014/ip2region
 */
public final class AddressUtils {
    private static final Logger log = LoggerFactory.getLogger(AddressUtils.class);

    public static final String UNKNOWN = "未知";

    private AddressUtils() {
    }

    /**
     * 查询 IP 对应的归属地。
     * 内网地址直接返回标记；查询未启用、地址无效或查询失败时返回未知。
     *
     * @param ip IPv4 或 IPv6 地址
     * @return 省市或海外地区名称
     */
    public static String getRealAddressByIP(String ip) {
        if (IpUtils.isUnknown(ip)) {
            return UNKNOWN;
        }
        try {
            // 只解析 IP 字面量，不进行域名解析；同时将映射的 IPv6 地址还原为 IPv4。
            InetAddress address = InetAddress.getByAddress(Util.parseIP(ip.trim()));
            if (IpUtils.internalIp(address.getHostAddress())) {
                return "内网IP";
            }
            if (!AppConfig.isAddressEnabled() || RegionHolder.SEARCHER == null) {
                return UNKNOWN;
            }
            return formatAddress(RegionHolder.SEARCHER.search(address.getAddress()));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return UNKNOWN;
        } catch (Exception e) {
            log.warn("查询 IP 归属地失败，IP：{}，原因：{}", ip, e.getMessage());
            return UNKNOWN;
        }
    }

    /**
     * 加载随应用打包的 IPv4、IPv6 地址库。
     * 全内存查询支持并发复用，加载后关闭输入流，不保留文件句柄或查询线程池。
     *
     * @return 查询服务，地址库加载失败时返回 null
     */
    private static Ip2Region createSearcher() {
        try (InputStream ipv4 = new ClassPathResource("ip2region/ip2region_v4.xdb").getInputStream();
             InputStream ipv6 = new ClassPathResource("ip2region/ip2region_v6.xdb").getInputStream()) {
            Config v4Config = Config.custom()
                    .setCachePolicy(Config.BufferCache)
                    .setXdbInputStream(ipv4)
                    .asV4();
            Config v6Config = Config.custom()
                    .setCachePolicy(Config.BufferCache)
                    .setXdbInputStream(ipv6)
                    .asV6();
            return Ip2Region.create(v4Config, v6Config);
        } catch (Exception e) {
            log.error("加载 ip2region 地址库失败，归属地将显示为未知", e);
            return null;
        }
    }

    /**
     * 整理地址库返回的地区字段。
     * 数据格式为“国家|省份|城市|运营商|国家代码”；国内展示省市，海外保留国家名称。
     *
     * @param region 地址库的原始查询结果
     * @return 去除空值和重复地区名称后的地址，无有效地区时返回未知
     */
    private static String formatAddress(String region) {
        if (StrUtil.isBlank(region)) {
            return UNKNOWN;
        }
        String[] fields = region.split("\\|", -1);
        if (fields.length < 3) {
            return UNKNOWN;
        }
        String country = normalizeAddress(fields[0]);
        Set<String> parts = new LinkedHashSet<>();
        if (!country.isEmpty() && !"中国".equals(country)) {
            parts.add(country);
        }
        for (int index = 1; index <= 2; index++) {
            String part = normalizeAddress(fields[index]);
            if (!part.isEmpty()) {
                parts.add(part);
            }
        }
        if (parts.isEmpty()) {
            return country.isEmpty() ? UNKNOWN : country;
        }
        return String.join(" ", parts);
    }

    /**
     * 清理地址库中的空值占位符。
     *
     * @param value 地区字段
     * @return 有效地区名称，空值占位符返回空字符串
     */
    private static String normalizeAddress(String value) {
        String address = StrUtil.trimToEmpty(value);
        return StrUtil.equalsAnyIgnoreCase(address, "0", "null", "unknown", "XX", "XX XX")
                ? "" : address;
    }

    /**
     * 首次启用归属地查询时加载地址库，后续请求复用同一个查询服务。
     * 数据来源：lionsoul2014/ip2region，提交 200f4cc7a6452843ef515a889c72d295281e8da3。
     */
    private static final class RegionHolder {
        private static final Ip2Region SEARCHER = createSearcher();
    }
}
