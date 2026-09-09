package dev.geo.admin.security.event;


import dev.geo.admin.common.constant.Constants;
import dev.geo.admin.common.utils.ServletUtils;
import dev.geo.admin.common.utils.ip.IpUtils;

public record LoginAuditEvent(
        String userName,
        String status,
        String message,
        String ip,
        String userAgent) {

    public LoginAuditEvent(String userName, String status, String message) {
        this(
                userName,
                status,
                message,
                IpUtils.getIp(),
                ServletUtils.getRequest().getHeader("User-Agent")
        );
    }

    public static LoginAuditEvent failure(String userName, String message) {
        return new LoginAuditEvent(userName, Constants.LOGIN_FAIL, message);
    }

    public static LoginAuditEvent success(String userName, String message) {
        return new LoginAuditEvent(userName, Constants.LOGIN_SUCCESS, message);
    }
}
