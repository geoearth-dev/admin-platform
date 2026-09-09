package dev.geo.admin.common.core.controller;

import cn.hutool.core.util.StrUtil;
import dev.geo.admin.common.core.model.ApiResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseController {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 返回成功
     */
    public ApiResult<Void> success() {
        return ApiResult.success();
    }

    /**
     * 返回失败消息
     */
    public ApiResult<Void> error() {
        return ApiResult.error();
    }

    /**
     * 返回成功消息
     */
    public ApiResult<Void> success(String message) {
        return ApiResult.success(message);
    }

    /**
     * 返回成功消息
     */
    public <T> ApiResult<T> success(T data) {
        return ApiResult.success(data);
    }

    /**
     * 返回失败消息
     */
    public ApiResult<Void> error(String message) {
        return ApiResult.error(message);
    }

    /**
     * 返回警告消息
     */
    public ApiResult<Void> warn(String message) {
        return ApiResult.warn(message);
    }

    /**
     * 响应返回结果
     *
     * @param rows 影响行数
     * @return 操作结果
     */
    protected ApiResult<Void> toApiResult(int rows) {
        return rows > 0 ? ApiResult.success() : ApiResult.error();
    }

    /**
     * 响应返回结果
     *
     * @param result 结果
     * @return 操作结果
     */
    protected ApiResult<Void> toApiResult(boolean result) {
        return result ? success() : error();
    }

    /**
     * 页面跳转
     */
    public String redirect(String url) {
        return StrUtil.format("redirect:{}", url);
    }


}
