package dev.geo.admin.system.service.monitor.impl;

import cn.hutool.core.util.StrUtil;
import dev.geo.admin.common.utils.ip.AddressUtils;
import dev.geo.admin.security.model.LoginSession;
import dev.geo.admin.security.session.LoginSessionStore;
import dev.geo.admin.system.model.monitor.dto.OnlineSessionQueryDTO;
import dev.geo.admin.system.model.monitor.entity.SysUserOnline;
import dev.geo.admin.system.service.monitor.ISysUserOnlineService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

/**
 * 在线会话服务实现。
 */
@Service
@RequiredArgsConstructor
public class SysUserOnlineServiceImpl implements ISysUserOnlineService {
    private final LoginSessionStore sessionStore;

    @Override
    public List<SysUserOnline> getOnlineSessions(OnlineSessionQueryDTO query) {
        return sessionStore.findAll().stream()
                .filter(session -> StrUtil.isBlank(query.getUserName())
                        || StrUtil.containsIgnoreCase(session.userInfo().username(), query.getUserName()))
                .filter(session -> StrUtil.isBlank(query.getIpAddress())
                        || StrUtil.contains(session.userInfo().ip(), query.getIpAddress()))
                .sorted(Comparator.comparing(LoginSession::loginAt).reversed())
                .map(this::toOnlineUser)
                .toList();
    }

    @Override
    public void terminateSession(String sessionId) {
        sessionStore.delete(sessionId);
    }

    private SysUserOnline toOnlineUser(LoginSession session) {
        SysUserOnline online = new SysUserOnline();
        online.setTokenId(session.sessionId());
        online.setDeptName(session.userInfo().deptName());
        online.setUserName(session.userInfo().username());
        online.setIp(session.userInfo().ip());
        online.setLoginLocation(AddressUtils.getRealAddressByIP(session.userInfo().ip()));
        online.setBrowser(session.userInfo().browser());
        online.setOs(session.userInfo().os());
        online.setLoginTime(session.loginAt().toEpochMilli());
        return online;
    }
}
