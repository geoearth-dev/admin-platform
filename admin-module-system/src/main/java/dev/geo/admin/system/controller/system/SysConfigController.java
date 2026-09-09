package dev.geo.admin.system.controller.system;

import cn.hutool.core.bean.BeanUtil;
import dev.geo.admin.common.annotation.Log;
import dev.geo.admin.common.core.controller.BaseController;
import dev.geo.admin.common.core.model.ApiResult;
import dev.geo.admin.common.core.model.PageResult;
import dev.geo.admin.common.enums.BusinessType;
import dev.geo.admin.excel.core.ExcelService;
import dev.geo.admin.mybatis.model.page.PageParam;
import dev.geo.admin.system.model.system.dto.SysConfigPageReqDTO;
import dev.geo.admin.system.model.system.dto.ConfigSaveDTO;
import dev.geo.admin.system.model.system.entity.SysConfig;
import dev.geo.admin.system.service.system.ISysConfigService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 系统参数配置接口。
 */
@RestController
@RequestMapping("/system/config")
@RequiredArgsConstructor
public class SysConfigController extends BaseController {
    private final ISysConfigService configService;
    private final ExcelService excelService;

    /**
     * 分页查询参数配置。
     */
    @PreAuthorize("@se.hasPermission('system:config:list')")
    @GetMapping("/list")
    public ApiResult<PageResult<SysConfig>> list(@Validated SysConfigPageReqDTO query) {
        return ApiResult.success(configService.getConfigPage(query));
    }

    @Log(title = "参数管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@se.hasPermission('system:config:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, @Validated SysConfigPageReqDTO query) {
        query.setPageSize(PageParam.PAGE_SIZE_NONE);
        PageResult<SysConfig> pageResult = configService.getConfigPage(query);
        excelService.exportExcel(response, pageResult.getRecords(), SysConfig.class, "参数数据");
    }

    /**
     * 查询参数配置详情。
     */
    @PreAuthorize("@se.hasPermission('system:config:query')")
    @GetMapping("/{id}")
    public ApiResult<SysConfig> getInfo(@PathVariable Long id) {
        return success(configService.getConfigById(id));
    }

    /**
     * 根据参数键名查询参数值。
     */
    @GetMapping(value = "/configKey/{configKey}")
    public ApiResult<String> getConfigKey(@PathVariable String configKey) {
        return ApiResult.success("操作成功", configService.selectConfigByKey(configKey));
    }

    /**
     * 新增参数配置。
     */
    @PreAuthorize("@se.hasPermission('system:config:add')")
    @Log(title = "参数管理", businessType = BusinessType.INSERT)
    @PostMapping
    public ApiResult<Void> add(@Validated @RequestBody ConfigSaveDTO request) {
        SysConfig config = BeanUtil.toBean(request, SysConfig.class);
        return toApiResult(configService.createConfig(config));
    }

    /**
     * 修改参数配置。
     */
    @PreAuthorize("@se.hasPermission('system:config:edit')")
    @Log(title = "参数管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public ApiResult<Void> edit(@Validated @RequestBody ConfigSaveDTO request) {
        SysConfig config = BeanUtil.toBean(request, SysConfig.class);
        return toApiResult(configService.updateConfig(config));
    }

    /**
     * 批量删除参数配置。
     */
    @PreAuthorize("@se.hasPermission('system:config:remove')")
    @Log(title = "参数管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public ApiResult<Void> remove(@PathVariable Long[] ids) {
        configService.deleteConfigs(ids);
        return success();
    }

    /**
     * 刷新参数缓存。
     */
    @PreAuthorize("@se.hasPermission('system:config:remove')")
    @Log(title = "参数管理", businessType = BusinessType.CLEAN)
    @DeleteMapping("/refreshCache")
    public ApiResult<Void> refreshCache() {
        configService.resetConfigCache();
        return success();
    }
}
