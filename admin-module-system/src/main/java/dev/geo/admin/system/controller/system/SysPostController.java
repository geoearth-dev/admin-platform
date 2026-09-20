package dev.geo.admin.system.controller.system;

import cn.hutool.core.bean.BeanUtil;
import dev.geo.admin.common.annotation.Log;
import dev.geo.admin.common.core.controller.BaseController;
import dev.geo.admin.common.core.model.ApiResult;
import dev.geo.admin.common.enums.BusinessType;
import dev.geo.admin.excel.core.ExcelService;
import dev.geo.admin.common.core.model.PageResult;
import dev.geo.admin.mybatis.model.page.PageParam;
import dev.geo.admin.system.model.system.dto.PostPageReqDTO;
import dev.geo.admin.system.model.system.dto.PostSaveDTO;
import dev.geo.admin.system.model.system.entity.SysPost;
import dev.geo.admin.system.service.system.ISysPostService;
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
 * 岗位管理接口。
 */
@Tag(name = "岗位管理")
@RestController
@RequestMapping("/system/post")
@RequiredArgsConstructor
public class SysPostController extends BaseController {
    private final ISysPostService postService;
    private final ExcelService excelService;

    @PreAuthorize("@se.hasPermission('system:post:list')")
    @GetMapping("/list")
    @Operation(summary = "分页查询岗位")
    public ApiResult<PageResult<SysPost>> list(@Validated @ParameterObject PostPageReqDTO query) {
        return success(postService.selectPostPage(query));
    }

    @Log(title = "岗位管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@se.hasPermission('system:post:export')")
    @PostMapping("/export")
    @Operation(summary = "导出岗位")
    public void export(HttpServletResponse response, @Validated @ParameterObject PostPageReqDTO query) {
        query.setPageSize(PageParam.PAGE_SIZE_NONE);
        excelService.exportExcel(response, postService.selectPostPage(query).getRecords(), SysPost.class, "岗位数据");
    }

    @PreAuthorize("@se.hasPermission('system:post:query')")
    @GetMapping("/{id}")
    @Operation(summary = "查询岗位详情")
    public ApiResult<SysPost> getInfo(@Parameter(description = "岗位 ID") @PathVariable Long id) {
        return success(postService.selectPostById(id));
    }

    @PreAuthorize("@se.hasPermission('system:post:add')")
    @Log(title = "岗位管理", businessType = BusinessType.INSERT)
    @PostMapping
    @Operation(summary = "新增岗位")
    public ApiResult<Void> add(@Validated @RequestBody PostSaveDTO request) {
        SysPost post = BeanUtil.toBean(request, SysPost.class);
        ApiResult<Void> validation = validatePost(post, "新增");
        return validation == null ? toApiResult(postService.insertPost(post)) : validation;
    }

    @PreAuthorize("@se.hasPermission('system:post:edit')")
    @Log(title = "岗位管理", businessType = BusinessType.UPDATE)
    @PutMapping
    @Operation(summary = "修改岗位")
    public ApiResult<Void> edit(@Validated @RequestBody PostSaveDTO request) {
        SysPost post = BeanUtil.toBean(request, SysPost.class);
        ApiResult<Void> validation = validatePost(post, "修改");
        return validation == null ? toApiResult(postService.updatePost(post)) : validation;
    }

    @PreAuthorize("@se.hasPermission('system:post:remove')")
    @Log(title = "岗位管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    @Operation(summary = "删除岗位")
    public ApiResult<Void> remove(@Parameter(description = "岗位 ID 列表，多个用逗号分隔") @PathVariable Long[] ids) {
        return toApiResult(postService.deletePostByIds(ids));
    }

    @GetMapping("/options")
    @Operation(summary = "查询岗位选项")
    public ApiResult<List<SysPost>> options() {
        return success(postService.selectPostAll());
    }

    private ApiResult<Void> validatePost(SysPost post, String action) {
        if (!postService.checkPostNameUnique(post)) {
            return error(action + "岗位失败，岗位名称已存在：" + post.getPostName());
        }
        if (!postService.checkPostCodeUnique(post)) {
            return error(action + "岗位失败，岗位编码已存在：" + post.getPostCode());
        }
        return null;
    }
}
