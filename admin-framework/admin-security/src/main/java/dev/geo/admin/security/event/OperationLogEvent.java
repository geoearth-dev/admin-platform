package dev.geo.admin.security.event;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;


@Getter
@Setter
public class OperationLogEvent {
    /**
     * 日志主键
     */
    private Long id;

    /**
     * 操作模块
     */
    private String title;

    /**
     * 业务类型（0其它 1新增 2修改 3删除）
     */
    private Integer businessType;

    /**
     * 业务类型数组
     */
    private Integer[] businessTypes;

    /**
     * 请求方法
     */
    private String methodName;

    /**
     * 请求方式
     */
    private String httpMethod;

    /**
     * 操作类别（0其它 1后台用户 2手机端用户）
     */
    private Integer operatorType;

    /**
     * 操作人员
     */
    private String userName;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 请求url
     */
    private String requestUri;

    /**
     * 操作地址
     */
    private String ipAddress;

    /**
     * 请求参数
     */
    private String requestParams;

    /**
     * 返回参数
     */
    private String responseBody;

    /**
     * 操作状态（0正常 1异常）
     */
    private Integer status;

    /**
     * 错误消息
     */
    private String errorMessage;

    /**
     * 消耗时间
     */
    private Long durationMs;
}
