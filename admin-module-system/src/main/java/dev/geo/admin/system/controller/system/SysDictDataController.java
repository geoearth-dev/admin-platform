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
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典数据管理接口。
 */
@RestController
@RequestMapping("/system/dict-data")
@RequiredArgsConstructor
public class SysDictDataController extends BaseController {
    private final ISysDictDataService dictDataService;
    private final ISysDictTypeService dictTypeService;
    private final ExcelService excelService;

    @PreAuthorize("@se.hasPermission('system:dict:list')")
    @GetMapping("/list")
    public ApiResult<PageResult<SysDictData>> list(@Validated DictDataPageReqDTO query) {
        return success(dictDataService.selectDictDataPage(query));
    }

    @Log(title = "字典数据", businessType = BusinessType.EXPORT)
    @PreAuthorize("@se.hasPermission('system:dict:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, @Validated DictDataPageReqDTO query) {
        query.setPageSize(PageParam.PAGE_SIZE_NONE);
        excelService.exportExcel(response, dictDataService.selectDictDataPage(query).getRecords(),
                SysDictData.class, "字典数据");
    }

    @PreAuthorize("@se.hasPermission('system:dict:query')")
    @GetMapping("/{id}")
    public ApiResult<SysDictData> getInfo(@PathVariable Long id) {
        return success(dictDataService.selectDictDataById(id));
    }

    @GetMapping("/type/{dictType}")
    public ApiResult<List<SysDictData>> getByType(@PathVariable String dictType) {
        return success(dictTypeService.selectDictDataByType(dictType));
    }

    @PreAuthorize("@se.hasPermission('system:dict:add')")
    @Log(title = "字典数据", businessType = BusinessType.INSERT)
    @PostMapping
    public ApiResult<Void> add(@Validated @RequestBody DictDataSaveDTO request) {
        SysDictData dictData = BeanUtil.toBean(request, SysDictData.class);
        return toApiResult(dictDataService.insertDictData(dictData));
    }

    @PreAuthorize("@se.hasPermission('system:dict:edit')")
    @Log(title = "字典数据", businessType = BusinessType.UPDATE)
    @PutMapping
    public ApiResult<Void> edit(@Validated @RequestBody DictDataSaveDTO request) {
        SysDictData dictData = BeanUtil.toBean(request, SysDictData.class);
        return toApiResult(dictDataService.updateDictData(dictData));
    }

    @PreAuthorize("@se.hasPermission('system:dict:remove')")
    @Log(title = "字典数据", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public ApiResult<Void> remove(@PathVariable Long[] ids) {
        dictDataService.deleteDictDataByIds(ids);
        return success();
    }
}
