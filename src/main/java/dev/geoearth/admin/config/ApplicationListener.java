package dev.geoearth.admin.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.time.Duration;
import java.util.Enumeration;

@Slf4j
@Component
public class ApplicationListener {

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady(ApplicationReadyEvent event) {

        Environment env = event.getApplicationContext().getEnvironment();
        // 获取应用名称
        String appName = env.getProperty("spring.application.name", "application");
        // 获取端口
        String port = env.getProperty("local.server.port", "8080def");

        // 判断是否启用 HTTPS
        String protocol = (env.getProperty("server.ssl.key-store") != null) ? "https" : "http";

        // 获取本机 IP
        String externalIp = getLocalIp();

        String apiPath = env.getProperty("scalar.path", "/api");

        // 获取激活的 profile（环境）
        String[] activeProfiles = env.getActiveProfiles();
        String profiles = activeProfiles.length == 0 ? "[]" : String.join(", ", activeProfiles);

        Duration timeTaken = event.getTimeTaken();
        long timeMillis = timeTaken == null ? 0 : timeTaken.toMillis();
        log.info("""
                        
                        ------------------------------------------------------------
                           {} 启动成功
                           本机地址：{}://localhost:{}
                           服务地址：{}://{}:{}
                           接口文档：{}://{}:{}{}
                           配置环境：{}
                           启动耗时：{} ms
                        ------------------------------------------------------------
                        """,
                appName,
                protocol, port,
                protocol, externalIp, port,
                protocol, externalIp, port, apiPath,
                profiles,
                timeMillis
        );
    }

    /**
     * 获取首选的本地 IPv4 地址（跳过虚拟网卡、回环、链路本地等）
     */
    public String getLocalIp() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                // 跳过未启用或回环接口
                if (!networkInterface.isUp() || networkInterface.isLoopback() || networkInterface.isVirtual()) {
                    continue;
                }
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();

                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    // 跳过不想要的网段
                    if (address instanceof Inet4Address && !address.isLoopbackAddress()) {
                        return address.getHostAddress();
                    }
                }
            }
            // 如果没找到合适的，退回到 localhost
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            log.warn("Failed to determine preferred IP address, falling back to localhost", e);
            return "127.0.0.1";
        }
    }
}
