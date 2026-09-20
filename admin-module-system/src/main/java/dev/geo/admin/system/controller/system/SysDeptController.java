package dev.geo.admin.system.controller.system;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import dev.geo.admin.common.annotation.Log;
import dev.geo.admin.common.constant.UserConstants;
import dev.geo.admin.common.core.controller.BaseController;
import dev.geo.admin.common.core.model.ApiResult;
import dev.geo.admin.common.enums.BusinessType;
import dev.geo.admin.system.model.system.entity.SysDept;
import dev.geo.admin.system.model.system.dto.DepartmentSaveDTO;
import dev.geo.admin.system.service.system.ISysDeptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * 部门管理接口。
 */
@Tag(name = "部门管理")
@RestController
@RequestMapping("/system/dept")
@RequiredArgsConstructor
public class SysDeptController extends BaseController {
    private final ISysDeptService deptService;

    @PreAuthorize("@se.hasPermission('system:dept:list')")
    @GetMapping("/list")
    @Operation(summary = "查询部门列表")
    public ApiResult<List<SysDept>> list(SysDept query) {
        return success(deptService.selectDeptList(query));
    }

    /**
     * 查询部门列表，并排除指定部门及其全部子部门。
     */
    @PreAuthorize("@se.hasPermission('system:dept:list')")
    @GetMapping("/list/exclude/{id}")
    @Operation(summary = "查询可选上级部门", description = "排除当前部门及其下级，避免把部门移动到自身分支下。")
    public ApiResult<List<SysDept>> excludeChild(@Parameter(description = "部门 ID") @PathVariable Long id) {
        List<SysDept> departments = deptService.selectDeptList(new SysDept());
        departments.removeIf(dept -> Objects.equals(dept.getId(), id)
                || StrUtil.split(dept.getAncestors(), ',').contains(String.valueOf(id)));
        return success(departments);
    }

    @PreAuthorize("@se.hasPermission('system:dept:query')")
    @GetMapping("/{id}")
    @Operation(summary = "查询部门详情")
    public ApiResult<SysDept> getInfo(@Parameter(description = "部门 ID") @PathVariable Long id) {
        deptService.checkDeptDataScope(id);
        return success(deptService.selectDeptById(id));
    }

    @PreAuthorize("@se.hasPermission('system:dept:add')")
    @Log(title = "部门管理", businessType = BusinessType.INSERT)
    @PostMapping
    @Operation(summary = "新增部门")
    public ApiResult<Void> add(@Validated @RequestBody DepartmentSaveDTO request) {
        SysDept dept = BeanUtil.toBean(request, SysDept.class);
        if (!deptService.checkDeptNameUnique(dept)) {
            return error("新增部门失败，部门名称已存在：" + dept.getDeptName());
        }
        return toApiResult(deptService.insertDept(dept));
    }

    @PreAuthorize("@se.hasPermission('system:dept:edit')")
    @Log(title = "部门管理", businessType = BusinessType.UPDATE)
    @PutMapping
    @Operation(summary = "修改部门")
    public ApiResult<Void> edit(@Validated @RequestBody DepartmentSaveDTO request) {
        SysDept dept = BeanUtil.toBean(request, SysDept.class);
        Long id = dept.getId();
        deptService.checkDeptDataScope(id);
        if (!deptService.checkDeptNameUnique(dept)) {
            return error("修改部门失败，部门名称已存在：" + dept.getDeptName());
        }
        if (Objects.equals(dept.getParentId(), id)) {
            return error("上级部门不能是当前部门自身");
        }
        if (UserConstants.DEPT_DISABLE.equals(dept.getStatus())
                && deptService.selectNormalChildrenDeptById(id) > 0) {
            return error("当前部门包含未停用的子部门，不能停用");
        }
        return toApiResult(deptService.updateDept(dept));
    }

    @PreAuthorize("@se.hasPermission('system:dept:remove')")
    @Log(title = "部门管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    @Operation(summary = "删除部门")
    public ApiResult<Void> remove(@Parameter(description = "部门 ID") @PathVariable Long id) {
        if (deptService.hasChildByDeptId(id)) {
            return warn("当前部门存在下级部门，不能删除");
        }
        if (deptService.checkDeptExistUser(id)) {
            return warn("当前部门存在关联用户，不能删除");
        }
        deptService.checkDeptDataScope(id);
        return toApiResult(deptService.deleteDeptById(id));
    }
}
