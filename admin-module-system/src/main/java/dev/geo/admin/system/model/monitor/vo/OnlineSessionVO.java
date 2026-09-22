package dev.geo.admin.system.model.monitor.vo;

/** 在线会话展示信息，不对应数据库表。 */
@lombok.Getter
@lombok.Setter
public class OnlineSessionVO {
    /** 会话编号 */
    private String sessionId;

    private Long userId;

    private String nickName;

    /** 当前请求使用的会话，前端无需解析访问令牌。 */
    private boolean currentSession;

    /** 会话级操作限制；接口权限仍由控制器校验。 */
    private boolean forceLogoutAllowed;

    /** 部门名称 */
    private String deptName;

    /** 用户名称 */
    private String userName;

    /** 登录IP地址 */
    private String ip;

    /** 登录地址 */
    private String loginLocation;

    /** 浏览器类型 */
    private String browser;

    /** 操作系统 */
    private String os;

    /** 登录时间 */
    private Long loginTime;

}
