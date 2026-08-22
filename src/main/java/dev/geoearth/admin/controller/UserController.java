package dev.geoearth.admin.controller;

import dev.geoearth.admin.common.core.entity.PageParam;
import dev.geoearth.admin.common.core.response.ApiResponse;
import dev.geoearth.admin.common.core.response.PageResponse;
import dev.geoearth.admin.model.dto.UserPageQuery;
import dev.geoearth.admin.model.vo.UserVO;
import dev.geoearth.admin.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "用户接口", description = "用户信息查询接口")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "分页查询用户")
    public ApiResponse<PageResponse<UserVO>> pageUsers(@Valid @ParameterObject PageParam pageParam,
                                                       @ParameterObject UserPageQuery query) {
        log.info("分页查询用户，pageNum={}，pageSize={}，name={}", pageParam.getPageNum(), pageParam.getPageSize(), query.getName());
        return ApiResponse.success(userService.pageUsers(pageParam, query));
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询用户")
    public ApiResponse<UserVO> getUserById(
            @Parameter(description = "用户ID", example = "1")
            @PathVariable Long id) {

        log.info("根据ID查询用户，id={}", id);

        return ApiResponse.success(userService.getUserById(id));
    }

    @GetMapping("/after")
    @Operation(summary = "查询指定时间之后创建的用户")
    public ApiResponse<List<UserVO>> listUsersCreatedAfter(
            @Parameter(description = "创建时间", required = true, example = "2026-07-11T10:00:00")
            @RequestParam("created_at")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime createdAt) {

        log.info("查询指定时间之后创建的用户，createdAt={}", createdAt);

        return ApiResponse.success(
                userService.listUsersCreatedAfter(createdAt)
        );
    }
}