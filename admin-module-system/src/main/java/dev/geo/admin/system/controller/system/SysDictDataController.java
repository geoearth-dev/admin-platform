package dev.geo.admin.system.controller.system;

import cn.hutool.core.bean.BeanUtil;
import dev.geo.admin.common.annotation.Log;
import dev.geo.admin.common.core.controller.BaseController;
import dev.geo.admin.common.core.model.ApiResult;
import dev.geo.admin.common.enums.BusinessType;
import dev.geo.admin.excel.core.ExcelService;
import dev.geo.admin.common.core.model.PageResult;
import dev.geo.admin.mybatis.model.page.PageParam;
import dev.geo.admin.system.model.system.dto.DictDataPageReqDTO;
import dev.geo.admin.system.model.system.dto.DictDataSaveDTO;
import dev.geo.admin.system.model.system.entity.SysDictData;
import dev.geo.admin.system.service.system.ISysDictDataService;
import dev.geo.admin.system.service.system.ISysDictTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典数据管理接口。
 */
@Tag(name = "字典数据")
@RestController
@RequestMapping("/system/dict-data")
@RequiredArgsConstructor
public class SysDictDataController extends BaseController {
    private final ISysDictDataService dictDataService;
    private final ISysDictTypeService dictTypeService;
    private final ExcelService excelService;

    @PreAuthorize("@se.hasPermission('system:dict:list')")
    @GetMapping("/list")
    @Operation(summary = "分页查询字典数据")
    public ApiResult<PageResult<SysDictData>> list(@Validated @ParameterObject DictDataPageReqDTO query) {
        return success(dictDataService.selectDictDataPage(query));
    }

    @Log(title = "字典数据", businessType = BusinessType.EXPORT)
    @PreAuthorize("@se.hasPermission('system:dict:export')")
    @PostMapping("/export")
    @Operation(summary = "导出字典数据")
    public void export(HttpServletResponse response, @Validated @ParameterObject DictDataPageReqDTO query) {
        query.setPageSize(PageParam.PAGE_SIZE_NONE);
        excelService.exportExcel(response, dictDataService.selectDictDataPage(query).getRecords(),
                SysDictData.class, "字典数据");
    }

    @PreAuthorize("@se.hasPermission('system:dict:query')")
    @GetMapping("/{id}")
    @Operation(summary = "查询字典数据详情")
    public ApiResult<SysDictData> getInfo(@Parameter(description = "字典数据 ID") @PathVariable Long id) {
        return success(dictDataService.selectDictDataById(id));
    }

    @GetMapping("/type/{dictType}")
    @Operation(summary = "按字典类型查询选项")
    public ApiResult<List<SysDictData>> getByType(@Parameter(description = "字典类型标识") @PathVariable String dictType) {
        return success(dictTypeService.selectDictDataByType(dictType));
    }

    @PreAuthorize("@se.hasPermission('system:dict:add')")
    @Log(title = "字典数据", businessType = BusinessType.INSERT)
    @PostMapping
    @Operation(summary = "新增字典数据")
    public ApiResult<Void> add(@Validated @RequestBody DictDataSaveDTO request) {
        SysDictData dictData = BeanUtil.toBean(request, SysDictData.class);
        return toApiResult(dictDataService.insertDictData(dictData));
    }

    @PreAuthorize("@se.hasPermission('system:dict:edit')")
    @Log(title = "字典数据", businessType = BusinessType.UPDATE)
    @PutMapping
    @Operation(summary = "修改字典数据")
    public ApiResult<Void> edit(@Validated @RequestBody DictDataSaveDTO request) {
        SysDictData dictData = BeanUtil.toBean(request, SysDictData.class);
        return toApiResult(dictDataService.updateDictData(dictData));
    }

    @PreAuthorize("@se.hasPermission('system:dict:remove')")
    @Log(title = "字典数据", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    @Operation(summary = "删除字典数据")
    public ApiResult<Void> remove(@Parameter(description = "字典数据 ID 列表，多个用逗号分隔") @PathVariable Long[] ids) {
        dictDataService.deleteDictDataByIds(ids);
        return success();
    }
}
