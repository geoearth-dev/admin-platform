package dev.geo.admin.system.service.monitor.impl;

import cn.hutool.core.util.StrUtil;
import dev.geo.admin.common.exception.ServiceException;
import dev.geo.admin.security.model.LoginSession;
import dev.geo.admin.security.session.LoginSessionStore;
import dev.geo.admin.security.utils.SecurityUtils;
import dev.geo.admin.system.model.monitor.dto.OnlineSessionQueryDTO;
import dev.geo.admin.system.model.monitor.vo.OnlineSessionVO;
import dev.geo.admin.system.service.message.MessageSseService;
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
    private final MessageSseService messageSseService;
    @Override
    public List<OnlineSessionVO> getOnlineSessions(OnlineSessionQueryDTO query) {
        return messageSseService.onlineSessions().stream()
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
        if (SecurityUtils.getSessionId().equals(sessionId)) {
            throw new ServiceException("当前会话请使用退出登录操作");
        }
        sessionStore.find(sessionId).ifPresent(session -> {
            if (!canManageSession(session)) {
                throw new ServiceException("不允许强制下线超级管理员");
            }
        });
        // 撤销后现有访问令牌和刷新令牌同时失效，SSE 只负责通知客户端。
        sessionStore.delete(sessionId, true);
    }

    private boolean canManageSession(LoginSession session) {
        return SecurityUtils.isAdmin() || !SecurityUtils.isAdmin(session.userInfo().userId());
    }

    private OnlineSessionVO toOnlineUser(LoginSession session) {
        OnlineSessionVO online = new OnlineSessionVO();
        boolean currentSession = SecurityUtils.getSessionId().equals(session.sessionId());
        online.setSessionId(session.sessionId());
        online.setUserId(session.userInfo().userId());
        online.setNickName(session.userInfo().nickName());
        online.setCurrentSession(currentSession);
        online.setForceLogoutAllowed(!currentSession && canManageSession(session));
        online.setDeptName(session.userInfo().deptName());
        online.setUserName(session.userInfo().username());
        online.setIp(session.userInfo().ip());
        online.setLoginLocation(session.userInfo().loginLocation());
        online.setBrowser(session.userInfo().browser());
        online.setOs(session.userInfo().os());
        online.setLoginTime(session.loginAt().toEpochMilli());
        return online;
    }
}
