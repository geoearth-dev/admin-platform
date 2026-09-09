package dev.geo.admin.system.resolve.listener;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import dev.geo.admin.common.constant.Constants;
import dev.geo.admin.common.utils.LogUtils;
import dev.geo.admin.common.utils.http.UserAgentUtils;
import dev.geo.admin.common.utils.ip.AddressUtils;
import dev.geo.admin.security.event.LoginAuditEvent;
import dev.geo.admin.security.event.OperationLogEvent;
import dev.geo.admin.system.model.monitor.entity.SysLoginLog;
import dev.geo.admin.system.model.monitor.entity.SysOperationLog;
import dev.geo.admin.system.service.monitor.ISysLoginLogService;
import dev.geo.admin.system.service.monitor.ISysOperationLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditListener {
    private final ISysLoginLogService loginLogService;
    private final ISysOperationLogService operationLogService;

    @Async("logTaskExecutor")
    @EventListener
    public void handle(LoginAuditEvent event) {
        String username = event.userName();
        String status = event.status();
        String message = event.message();

        final String userAgent = event.userAgent();
        final String ip = event.ip();

        String address = AddressUtils.getRealAddressByIP(ip);
        String s = LogUtils.getBlock(ip) +
                address +
                LogUtils.getBlock(username) +
                LogUtils.getBlock(status) +
                LogUtils.getBlock(message);
        // 打印信息到日志
        log.info(s, message);
        // 获取客户端操作系统
        String os = UserAgentUtils.getOperatingSystem(userAgent);
        // 获取客户端浏览器
        String browser = UserAgentUtils.getBrowser(userAgent);
        // 封装对象
        SysLoginLog loginLog = new SysLoginLog();
        loginLog.setUserName(username);
        loginLog.setIpAddress(ip);
        loginLog.setLoginLocation(address);
        loginLog.setBrowser(browser);
        loginLog.setOperatingSystem(os);
        loginLog.setMessage(message);
        loginLog.setLoginTime(Instant.now());
        // 日志状态
        if (StrUtil.equalsAny(status, Constants.LOGIN_SUCCESS, Constants.LOGOUT, Constants.REGISTER)) {
            loginLog.setStatus(Constants.SUCCESS);
        } else if (Constants.LOGIN_FAIL.equals(status)) {
            loginLog.setStatus(Constants.FAIL);
        }
        // 插入数据
        loginLogService.insertLoginLog(loginLog);
    }

    /**
     * 操作日志记录
     *
     * @param event 操作日志信息
     */
    @Async("logTaskExecutor")
    @EventListener
    public void handle(final OperationLogEvent event) {
        SysOperationLog operationLog = BeanUtil.copyProperties(event, SysOperationLog.class);

        // 设置操作时间
        operationLog.setOperationTime(Instant.now());
        // 远程查询操作地点
        operationLog.setOperationLocation(AddressUtils.getRealAddressByIP(operationLog.getIpAddress()));
        operationLogService.insertOperationLog(operationLog);
    }
}
