package dev.geo.admin.security.aspectj;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import dev.geo.admin.common.annotation.Log;
import dev.geo.admin.common.enums.BusinessStatus;
import dev.geo.admin.common.enums.HttpMethod;
import dev.geo.admin.common.filter.PropertyPreExcludeFilter;
import dev.geo.admin.common.utils.ServletUtils;
import dev.geo.admin.common.utils.ip.IpUtils;
import dev.geo.admin.security.event.OperationLogEvent;
import dev.geo.admin.security.model.LoginPrincipal;
import dev.geo.admin.security.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.NamedThreadLocal;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * 操作日志记录处理
 *
 */
@Aspect
@Component
public class LogAspect {
    private static final Logger log = LoggerFactory.getLogger(LogAspect.class);

    /**
     * 排除敏感属性字段
     */
    public static final String[] EXCLUDE_PROPERTIES = {"password", "oldPassword", "newPassword", "confirmPassword"};

    /**
     * 计算操作消耗时间
     */
    private static final ThreadLocal<Long> TIME_THREADLOCAL = new NamedThreadLocal<>("Cost Time");
    private final ApplicationEventPublisher eventPublisher;

    public LogAspect(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    /**
     * 处理请求前执行
     */
    @Before(value = "@annotation(controllerLog)")
    public void boBefore(JoinPoint joinPoint, Log controllerLog) {
        TIME_THREADLOCAL.set(System.currentTimeMillis());
    }

    /**
     * 处理完请求后执行
     *
     * @param joinPoint 切点
     */
    @AfterReturning(pointcut = "@annotation(controllerLog)", returning = "result")
    public void doAfterReturning(JoinPoint joinPoint, Log controllerLog, Object result) {
        handleLog(joinPoint, controllerLog, null, result);
    }

    /**
     * 拦截异常操作
     *
     * @param joinPoint 切点
     * @param e         异常
     */
    @AfterThrowing(value = "@annotation(controllerLog)", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Log controllerLog, Exception e) {
        handleLog(joinPoint, controllerLog, e, null);
    }

    protected void handleLog(final JoinPoint joinPoint, Log controllerLog, final Exception e, Object result) {
        try {
            // 获取当前的用户
            LoginPrincipal loginUser = SecurityUtils.getLoginPrincipal();

            // *========数据库日志=========*//
            OperationLogEvent operationLogEvent = new OperationLogEvent();
            operationLogEvent.setStatus(BusinessStatus.SUCCESS.ordinal());
            // 请求的地址
            String ip = IpUtils.getIp();
            operationLogEvent.setIpAddress(ip);
            operationLogEvent.setRequestUri(StrUtil.sub(ServletUtils.getRequest().getRequestURI(), 0, 255));

            operationLogEvent.setDeptName(loginUser.getDeptName());

            if (e != null) {
                operationLogEvent.setStatus(BusinessStatus.FAIL.ordinal());
                operationLogEvent.setErrorMessage(StrUtil.sub(e.getMessage(), 0, 2000));
            }
            // 设置方法名称
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            operationLogEvent.setMethodName(className + "." + methodName + "()");
            // 设置请求方式
            operationLogEvent.setHttpMethod(ServletUtils.getRequest().getMethod());
            // 处理设置注解上的参数
            getControllerMethodDescription(joinPoint, controllerLog, operationLogEvent, result);
            // 设置消耗时间
            operationLogEvent.setDurationMs(System.currentTimeMillis() - TIME_THREADLOCAL.get());
            // 保存数据库
            eventPublisher.publishEvent(operationLogEvent);
        } catch (Exception exp) {
            // 记录本地异常日志
            log.error("记录操作日志发生异常", exp);
        } finally {
            TIME_THREADLOCAL.remove();
        }
    }

    /**
     * 获取注解中对方法的描述信息 用于Controller层注解
     *
     * @param log     日志
     * @param operationLog 操作日志
     * @throws Exception Exception
     */
    public void getControllerMethodDescription(JoinPoint joinPoint, Log log, OperationLogEvent operationLog, Object result) throws Exception {
        // 设置action动作
        operationLog.setBusinessType(log.businessType().ordinal());
        // 设置标题
        operationLog.setTitle(log.title());
        // 设置操作人类别
        operationLog.setOperatorType(log.operatorType().ordinal());
        // 是否需要保存request，参数和值
        if (log.isSaveRequestData()) {
            // 获取参数的信息，传入到数据库中。
            setRequestValue(joinPoint, operationLog, log.excludeParamNames());
        }
        // 是否需要保存response，参数和值
        if (log.isSaveResponseData() && ObjectUtil.isNotNull(result)) {
            operationLog.setResponseBody(StrUtil.sub(JSON.toJSONString(result), 0, 2000));
        }
    }

    /**
     * 获取请求的参数，放到log中
     *
     * @param operationLog 操作日志
     */
    private void setRequestValue(JoinPoint joinPoint, OperationLogEvent operationLog, String[] excludeParamNames) {
        Map<?, ?> paramsMap = ServletUtils.getParamMap(ServletUtils.getRequest());
        String requestMethod = operationLog.getHttpMethod();
        if (ObjectUtil.isEmpty(paramsMap)
                && (HttpMethod.PUT.name().equals(requestMethod) || HttpMethod.POST.name().equals(requestMethod))) {
            String params = argsArrayToString(joinPoint.getArgs(), excludeParamNames);
            operationLog.setRequestParams(StrUtil.sub(params, 0, 2000));
        } else {
            operationLog.setRequestParams(StrUtil.sub(JSON.toJSONString(paramsMap, excludePropertyPreFilter(excludeParamNames)), 0, 2000));
        }
    }

    /**
     * 参数拼装
     */
    private String argsArrayToString(Object[] paramsArray, String[] excludeParamNames) {
        StringBuilder params = new StringBuilder();
        if (paramsArray != null) {
            for (Object o : paramsArray) {
                if (ObjectUtil.isNotNull(o) && !isFilterObject(o)) {
                    String jsonObj = JSON.toJSONString(o, excludePropertyPreFilter(excludeParamNames));
                    params.append(jsonObj).append(" ");
                }
            }
        }
        return params.toString().trim();
    }

    /**
     * 忽略敏感属性
     */
    public PropertyPreExcludeFilter excludePropertyPreFilter(String[] excludeParamNames) {
        return new PropertyPreExcludeFilter().addExcludes(ArrayUtils.addAll(EXCLUDE_PROPERTIES, excludeParamNames));
    }

    /**
     * 判断是否需要过滤的对象。
     *
     * @param object 对象信息。
     * @return 如果是需要过滤的对象，则返回true；否则返回false。
     */
    public boolean isFilterObject(Object object) {
        if (object == null) {
            return false;
        }
        // 直接过滤的对象
        if (object instanceof MultipartFile
                || object instanceof HttpServletRequest
                || object instanceof HttpServletResponse
                || object instanceof BindingResult) {
            return true;
        }
        // 对象数组，例如 MultipartFile[]、Object[]
        if (object instanceof Object[] array) {
            return Arrays.stream(array).anyMatch(MultipartFile.class::isInstance);
        }

        // 集合
        if (object instanceof Collection<?> collection) {
            return collection.stream().anyMatch(MultipartFile.class::isInstance);
        }
        // Map，只查 value
        if (object instanceof Map<?, ?> map) {
            return map.values().stream().anyMatch(MultipartFile.class::isInstance);
        }
        return false;
    }
}
