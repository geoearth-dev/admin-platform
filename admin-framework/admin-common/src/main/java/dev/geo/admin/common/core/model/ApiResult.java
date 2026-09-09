package dev.geo.admin.common.core.model;

import dev.geo.admin.common.constant.HttpStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(description = "统一接口响应")
public class ApiResult<T> {

    @Schema(description = "响应代码，200表示成功", example = "200")
    private final int code;

    @Schema(description = "响应消息", example = "操作成功")
    private final String message;

    @Schema(description = "响应数据")
    private final T data;


    /**
     * 返回成功结果
     *
     * @return 成功结果
     */
    public static <T> ApiResult<T> success() {
        return ApiResult.success(null);
    }

    /**
     * 返回成功消息
     *
     * @param msg 成功消息
     * @return 成功结果
     */
    public static <T> ApiResult<T> success(String msg) {
        return new ApiResult<>(HttpStatus.SUCCESS, msg, null);
    }

    /**
     * 返回成功数据
     *
     * @param data 响应数据
     * @param <T>  响应数据类型
     * @return 成功结果
     */
    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>(200, "操作成功", data);
    }

    /**
     * 返回成功消息和数据
     *
     * @param msg  成功消息
     * @param data 响应数据
     * @param <T>  响应数据类型
     * @return 成功结果
     */
    public static <T> ApiResult<T> success(String msg, T data) {
        return new ApiResult<>(HttpStatus.SUCCESS, msg, data);
    }

    /**
     * 返回警告消息
     *
     * @param msg 返回内容
     * @return 警告消息
     */
    public static ApiResult<Void> warn(String msg) {
        return new ApiResult<>(HttpStatus.WARN, msg, null);
    }

    /**
     * 返回警告消息和数据
     *
     * @param msg  返回内容
     * @param data 数据对象
     * @return 警告消息
     */
    public static <T> ApiResult<T> warn(String msg, T data) {
        return new ApiResult<>(HttpStatus.WARN, msg, data);
    }

    /**
     * 返回错误消息
     *
     * @return 错误消息
     */
    public static ApiResult<Void> error() {
        return error("操作失败");
    }

    /**
     * 返回错误消息
     *
     * @param msg 返回内容
     * @return 错误消息
     */
    public static <T> ApiResult<T> error(String msg) {
        return error(HttpStatus.ERROR, msg);
    }


    /**
     * 返回错误消息和数据
     *
     * @param msg  错误消息
     * @param data 响应数据
     * @param <T>  响应数据类型
     * @return 错误结果
     */
    public static <T> ApiResult<T> error(String msg, T data) {
        return new ApiResult<>(HttpStatus.ERROR, msg, data);
    }


    /**
     * 返回指定状态码的错误结果
     *
     * @param code 状态码
     * @param msg  错误消息
     * @param <T>  响应数据类型
     * @return 错误结果
     */
    public static <T> ApiResult<T> error(int code, String msg) {
        return new ApiResult<>(code, msg, null);
    }

}
