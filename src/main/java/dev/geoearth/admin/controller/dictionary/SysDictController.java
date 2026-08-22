package dev.geoearth.admin.controller.dictionary;

import dev.geoearth.admin.common.annotation.Log;
import dev.geoearth.admin.common.core.entity.PageParam;
import dev.geoearth.admin.common.core.response.ApiResponse;
import dev.geoearth.admin.common.core.response.PageResponse;
import dev.geoearth.admin.common.enums.BusinessType;
import dev.geoearth.admin.model.dto.dictionary.DictDataPageQuery;
import dev.geoearth.admin.model.dto.dictionary.DictTypePageQuery;
import dev.geoearth.admin.model.entity.dictionary.SysDictData;
import dev.geoearth.admin.model.entity.dictionary.SysDictType;
import dev.geoearth.admin.model.vo.dictionary.DictOptionVO;
import dev.geoearth.admin.service.dictionary.SysDictService;
import dev.geoearth.admin.config.openapi.OpenApiTags;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dictionaries")
@Tag(name = OpenApiTags.SYS_DICT, description = OpenApiTags.SYS_DICT_DESCRIPTION)
public class SysDictController {

    private final SysDictService dicService;

    @GetMapping
    @Operation(summary = "分页查询字典类型列表", description = "分页查询系统中的全部字典类型")
    public ApiResponse<PageResponse<SysDictType>> listTypes(
            @Valid @ParameterObject PageParam pageParam,
            @ParameterObject DictTypePageQuery query) {
        log.info("分页查询字典类型，pageNum={}，pageSize={}，query={}", pageParam.getPageNum(), pageParam.getPageSize(), query);
        return ApiResponse.success(dicService.listTypes(pageParam, query));
    }

    @PostMapping
    @Log(title = "字典类型", businessType = BusinessType.INSERT)
    @Operation(summary = "新增字典类型", description = "创建新的系统字典类型")
    public ApiResponse<Integer> createType(@RequestBody SysDictType type) {
        return ApiResponse.success(dicService.createType(type));
    }

    @PutMapping
    @Log(title = "字典类型", businessType = BusinessType.UPDATE)
    @Operation(summary = "修改字典类型", description = "修改指定系统字典类型")
    public ApiResponse<Integer> updateType(@Validated @RequestBody SysDictType dict) {
        return ApiResponse.success(dicService.updateType(dict));
    }

    @PostMapping("/{dictId}/disable")
    @Log(title = "字典类型", businessType = BusinessType.DISABLE)
    @Operation(summary = "停用字典类型", description = "停用字典类型及其全部字典数据")
    public ApiResponse<Void> disableType(@PathVariable Long dictId) {
        dicService.disableType(dictId);
        return ApiResponse.success();
    }

    @PostMapping("/{dictId}/enable")
    @Log(title = "字典类型", businessType = BusinessType.ENABLE)
    @Operation(summary = "启用字典类型", description = "启用字典类型及其全部字典数据")
    public ApiResponse<Void> ableType(@PathVariable Long dictId) {
        dicService.enableType(dictId);
        return ApiResponse.success();
    }

    @GetMapping("/items")
    @Operation(summary = "分页查询字典数据列表", description = "分页查询系统中的全部字典数据")
    public ApiResponse<PageResponse<SysDictData>> listData(
            @Valid @ParameterObject PageParam pageParam,
            @ParameterObject DictDataPageQuery query
    ) {
        log.info("分页查询字典数据，pageNum={}，pageSize={}，query={}", pageParam.getPageNum(), pageParam.getPageSize(), query);
        return ApiResponse.success(dicService.listData(pageParam, query));
    }

    @PostMapping("/items")
    @Log(title = "字典数据", businessType = BusinessType.INSERT)
    @Operation(summary = "新增字典数据", description = "在指定字典类型下新增字典数据")
    public ApiResponse<Integer> createData(@Validated @RequestBody SysDictData data) {
        return ApiResponse.success(dicService.createData(data));
    }

    @PutMapping("/items")
    @Log(title = "字典数据", businessType = BusinessType.UPDATE)
    @Operation(summary = "修改字典数据", description = "修改指定字典数据")
    public ApiResponse<Integer> updateData(@Validated @RequestBody SysDictData data) {
        return ApiResponse.success(dicService.updateData(data));
    }

    @PostMapping("/items/{dictCode}/disable")
    @Log(title = "字典数据", businessType = BusinessType.DISABLE)
    @Operation(summary = "停用字典数据", description = "停用指定字典数据")
    public ApiResponse<Void> disableData(@PathVariable Long dictCode) {
        dicService.disableData(dictCode);
        return ApiResponse.success();
    }

    @PostMapping("/items/{dictCode}/enable")
    @Log(title = "字典数据", businessType = BusinessType.ENABLE)
    @Operation(summary = "启用字典数据", description = "启用指定字典数据")
    public ApiResponse<Void> enableData(@PathVariable Long dictCode) {
        dicService.enableData(dictCode);
        return ApiResponse.success();
    }

    @GetMapping("/options")
    @Operation(summary = "根据字典类型获取字典下拉列表", description = "业务端使用，做了缓存处理，优先从缓存查找options下拉选项")
    public ApiResponse<List<DictOptionVO>> options(
            @Parameter(description = "字典类型", example = "sys_user_sex", required = true)
            @RequestParam("dictType")
            @NotBlank(message = "字典类型不能为空")
            String dictType
    ) {
        return ApiResponse.success(dicService.getItems(dictType));
    }
}
