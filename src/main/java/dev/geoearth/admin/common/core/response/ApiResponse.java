package dev.geoearth.admin.common.core.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(description = "统一接口响应")
public class ApiResponse<T> {

    @Schema(description = "响应代码，200表示成功", example = "200")
    private final int code;

    @Schema(description = "响应消息", example = "操作成功")
    private final String message;

    @Schema(description = "响应数据")
    private final T data;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "操作成功", data);
    }

    public static ApiResponse<Void> success() {
        return new ApiResponse<>(200, "操作成功", null);
    }

    public static <T> ApiResponse<T> fail(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }
}