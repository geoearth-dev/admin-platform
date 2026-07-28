package com.xyy.sim.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "用户信息")
public class UserVO {

    @Schema(description = "用户ID", example = "1")
    private final Long id;

    @Schema(description = "用户名称", example = "张三")
    private final String name;

    @Schema(description = "电子邮箱", example = "zhangsan@example.com")
    private final String email;

    @Schema(description = "创建时间")
    private final LocalDateTime createdAt;
}