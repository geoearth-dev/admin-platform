package dev.geoearth.admin.common.core.controller;

import dev.geoearth.admin.common.core.entity.PageParam;
import dev.geoearth.admin.common.core.response.ApiResponse;
import dev.geoearth.admin.common.core.response.PageResponse;
import dev.geoearth.admin.common.core.service.CrudService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

/**
 * 配置类资源的标准CRUD接口。
 */
public abstract class AbstractCrudController<T, S extends CrudService<T>> {

    protected final S service;

    protected AbstractCrudController(S service) {
        this.service = service;
    }

    @GetMapping
    @Operation(
            summary = "分页查询",
            description = "分页查询当前资源，keyword可用于业务编码或名称的模糊匹配"
    )
    public ApiResponse<PageResponse<T>> page(
            @Valid @ParameterObject PageParam pageParam,
            @RequestParam(required = false) String keyword
    ) {
        return ApiResponse.success(service.page(pageParam, keyword));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询详情", description = "根据主键ID查询一条未删除的数据")
    public ApiResponse<T> get(@PathVariable Long id) {
        return ApiResponse.success(service.getRequired(id));
    }

    @PostMapping
    @Operation(summary = "新增", description = "新增一条数据，主键和审计时间由后台自动维护")
    public ApiResponse<T> create(@RequestBody T entity) {
        return ApiResponse.success(service.create(entity));
    }

    @PutMapping("/{id}")
    @Operation(summary = "修改", description = "根据主键ID修改业务字段")
    public ApiResponse<T> update(@PathVariable Long id, @RequestBody T entity) {
        return ApiResponse.success(service.update(id, entity));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除", description = "根据主键ID进行逻辑删除")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ApiResponse.success();
    }
}
