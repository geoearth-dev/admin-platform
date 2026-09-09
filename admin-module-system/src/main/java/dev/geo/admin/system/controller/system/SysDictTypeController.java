package dev.geo.admin.system.controller.system;

import cn.hutool.core.bean.BeanUtil;
import dev.geo.admin.common.annotation.Log;
import dev.geo.admin.common.core.controller.BaseController;
import dev.geo.admin.common.core.model.ApiResult;
import dev.geo.admin.common.core.model.PageResult;
import dev.geo.admin.common.enums.BusinessType;
import dev.geo.admin.excel.core.ExcelService;
import dev.geo.admin.mybatis.model.page.PageParam;
import dev.geo.admin.system.model.system.dto.DictTypePageReqDTO;
import dev.geo.admin.system.model.system.dto.DictTypeSaveDTO;
import dev.geo.admin.system.model.system.entity.SysDictType;
import dev.geo.admin.system.service.system.ISysDictTypeService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典类型管理接口。
 */
@RestController
@RequestMapping("/system/dict-type")
@RequiredArgsConstructor
public class SysDictTypeController extends BaseController {
    private final ISysDictTypeService dictTypeService;
    private final ExcelService excelService;

    @PreAuthorize("@se.hasPermission('system:dict:list')")
    @GetMapping("/list")
    public ApiResult<PageResult<SysDictType>> list(@Validated DictTypePageReqDTO query) {
        return success(dictTypeService.selectDictTypePage(query));
    }

    @Log(title = "字典类型", businessType = BusinessType.EXPORT)
    @PreAuthorize("@se.hasPermission('system:dict:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, @Validated DictTypePageReqDTO query) {
        query.setPageSize(PageParam.PAGE_SIZE_NONE);
        excelService.exportExcel(response, dictTypeService.selectDictTypePage(query).getRecords(),
                SysDictType.class, "字典类型");
    }

    @PreAuthorize("@se.hasPermission('system:dict:query')")
    @GetMapping("/{id}")
    public ApiResult<SysDictType> getInfo(@PathVariable Long id) {
        return success(dictTypeService.selectDictTypeById(id));
    }

    @PreAuthorize("@se.hasPermission('system:dict:add')")
    @Log(title = "字典类型", businessType = BusinessType.INSERT)
    @PostMapping
    public ApiResult<Void> add(@Validated @RequestBody DictTypeSaveDTO request) {
        SysDictType dictType = BeanUtil.toBean(request, SysDictType.class);
        if (!dictTypeService.checkDictTypeUnique(dictType)) {
            return error("新增字典类型失败，类型标识已存在：" + dictType.getDictType());
        }
        return toApiResult(dictTypeService.insertDictType(dictType));
    }

    @PreAuthorize("@se.hasPermission('system:dict:edit')")
    @Log(title = "字典类型", businessType = BusinessType.UPDATE)
    @PutMapping
    public ApiResult<Void> edit(@Validated @RequestBody DictTypeSaveDTO request) {
        SysDictType dictType = BeanUtil.toBean(request, SysDictType.class);
        if (!dictTypeService.checkDictTypeUnique(dictType)) {
            return error("修改字典类型失败，类型标识已存在：" + dictType.getDictType());
        }
        return toApiResult(dictTypeService.updateDictType(dictType));
    }

    @PreAuthorize("@se.hasPermission('system:dict:remove')")
    @Log(title = "字典类型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public ApiResult<Void> remove(@PathVariable Long[] ids) {
        dictTypeService.deleteDictTypeByIds(ids);
        return success();
    }

    @PreAuthorize("@se.hasPermission('system:dict:remove')")
    @Log(title = "字典类型", businessType = BusinessType.CLEAN)
    @DeleteMapping("/cache")
    public ApiResult<Void> refreshCache() {
        dictTypeService.resetDictCache();
        return success();
    }

    @GetMapping("/options")
    public ApiResult<List<SysDictType>> options() {
        return success(dictTypeService.selectDictTypeAll());
    }
}
