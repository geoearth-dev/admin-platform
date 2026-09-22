package dev.geo.admin.system.service.monitor;

import dev.geo.admin.system.model.monitor.dto.OnlineSessionQueryDTO;
import dev.geo.admin.system.model.monitor.vo.OnlineSessionVO;

import java.util.List;

/**
 * 在线会话服务。
 */
public interface ISysUserOnlineService {
    List<OnlineSessionVO> getOnlineSessions(OnlineSessionQueryDTO query);

    void terminateSession(String sessionId);
}
