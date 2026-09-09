package dev.geo.admin.common.exception.handler;


import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import dev.geo.admin.common.constant.HttpStatus;
import dev.geo.admin.common.core.model.ApiResult;
import dev.geo.admin.common.exception.DemoModeException;
import dev.geo.admin.common.exception.ServiceException;
import dev.geo.admin.common.exception.base.BaseException;
import dev.geo.admin.common.utils.I18nMessageUtil;
import dev.geo.admin.common.utils.html.EscapeUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final I18nMessageUtil messages;

    @ResponseStatus(org.springframework.http.HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({AuthenticationException.class, InvalidBearerTokenException.class})
    public ApiResult<Void> handleAuthenticationException(RuntimeException exception) {
        log.info("认证失败: {}", exception.getMessage());
        return ApiResult.error(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }

    /**
     * 权限校验异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(org.springframework.http.HttpStatus.FORBIDDEN)
    public ApiResult<Void> handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',权限校验失败'{}'", requestURI, e.getMessage());
        return ApiResult.error(HttpStatus.FORBIDDEN, "没有权限，请联系管理员授权");
    }

    /**
     * 请求方式不支持
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ApiResult<Void> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',不支持'{}'请求", requestURI, e.getMethod());
        return ApiResult.error(e.getMessage());
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(ServiceException.class)
    public ApiResult<Void> handleServiceException(ServiceException e, HttpServletRequest request) {
        log.error(e.getMessage(), e);
        String message;
        if (StrUtil.isNotEmpty(e.getI18nCode())) {
            message = messages.getOrDefault(e.getI18nCode(), e.getMessage(), e.getArgs());
        } else {
            message = e.getMessage();
        }
        return ApiResult.error(message);
    }

    /**
     * 请求路径中缺少必需的路径变量
     */
    @ExceptionHandler(MissingPathVariableException.class)
    public ApiResult<Void> handleMissingPathVariableException(MissingPathVariableException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求路径中缺少必需的路径变量'{}',发生系统异常.", requestURI, e);
        return ApiResult.error(String.format("请求路径中缺少必需的路径变量[%s]", e.getVariableName()));
    }

    /**
     * 请求参数类型不匹配
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ApiResult<Void> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String value = Convert.toStr(e.getValue());
        if (StrUtil.isNotEmpty(value)) {
            value = EscapeUtil.clean(value);
        }
        log.error("请求参数类型不匹配'{}',发生系统异常.", requestURI, e);
        String requiredTypeName = e.getRequiredType() != null ? e.getRequiredType().getName() : "";
        return ApiResult.error(String.format("请求参数类型不匹配，参数[%s]要求类型为：'%s'，但输入值为：'%s'", e.getName(), requiredTypeName, value));
    }

    /**
     * 拦截未知的运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public ApiResult<Void> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生未知异常.", requestURI, e);
        return ApiResult.error(e.getMessage());
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(Exception.class)
    public ApiResult<Void> handleException(Exception e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生系统异常.", requestURI, e);
        return ApiResult.error(e.getMessage());
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(BindException.class)
    public ApiResult<Void> handleBindException(BindException e) {
        log.error(e.getMessage(), e);
        String message = e.getAllErrors().get(0).getDefaultMessage();
        return ApiResult.error(message);
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = "";
        if (fieldError != null) {
            message = fieldError.getDefaultMessage();
        }
        return ApiResult.error(message);
    }

    /**
     * 演示模式异常
     */
    @ExceptionHandler(DemoModeException.class)
    public ApiResult<Void> handleDemoModeException(DemoModeException e) {
        return ApiResult.error("演示模式，不允许操作");
    }

    @ExceptionHandler(BaseException.class)
    public ApiResult<Void> handleBaseException(BaseException e) {
        String message;
        if (StrUtil.isNotEmpty(e.getCode())) {
            String defaultMessage = StrUtil.isNotEmpty(e.getDefaultMessage()) ? e.getDefaultMessage() : e.getCode();
            message = messages.getOrDefault(e.getCode(), defaultMessage, e.getArgs());
        } else {
            message = e.getDefaultMessage();
        }

        return ApiResult.error(message);
    }
}
