package dev.geo.admin.system.controller.system;

import cn.hutool.core.bean.BeanUtil;
import dev.geo.admin.common.annotation.Log;
import dev.geo.admin.common.core.controller.BaseController;
import dev.geo.admin.common.core.model.ApiResult;
import dev.geo.admin.common.enums.BusinessType;
import dev.geo.admin.common.core.model.PageResult;
import dev.geo.admin.security.utils.SecurityUtils;
import dev.geo.admin.system.model.system.dto.NoticePageReqDTO;
import dev.geo.admin.system.model.system.dto.NoticeSaveDTO;
import dev.geo.admin.system.model.system.dto.NoticeReadUserPageReqDTO;
import dev.geo.admin.system.model.system.entity.SysNotice;
import dev.geo.admin.system.model.system.vo.NoticeReadVO;
import dev.geo.admin.system.model.system.vo.NoticeReadUserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import dev.geo.admin.system.service.system.ISysNoticeReadService;
import dev.geo.admin.system.service.system.ISysNoticeService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 通知公告管理接口。
 */
@Tag(name = "通知公告")
@RestController
@RequestMapping("/system/notice")
@RequiredArgsConstructor
@Validated
public class SysNoticeController extends BaseController {
    private final ISysNoticeService noticeService;
    private final ISysNoticeReadService noticeReadService;

    @PreAuthorize("@se.hasPermission('system:notice:list')")
    @GetMapping("/list")
    @Operation(summary = "分页查询通知公告")
    public ApiResult<PageResult<SysNotice>> list(@Validated @ParameterObject NoticePageReqDTO query) {
        return success(noticeService.selectNoticePage(query));
    }

    @PreAuthorize("@se.hasPermission('system:notice:query')")
    @GetMapping("/{id}")
    @Operation(summary = "查询通知公告详情")
    public ApiResult<SysNotice> getInfo(@Parameter(description = "公告 ID") @PathVariable Long id) {
        return success(noticeService.selectNoticeById(id));
    }

    @PreAuthorize("@se.hasPermission('system:notice:add')")
    @Log(title = "通知公告", businessType = BusinessType.INSERT)
    @PostMapping
    @Operation(summary = "新增通知公告")
    public ApiResult<Void> add(@Validated @RequestBody NoticeSaveDTO request) {
        SysNotice notice = BeanUtil.toBean(request, SysNotice.class);
        return toApiResult(noticeService.insertNotice(notice));
    }

    @PreAuthorize("@se.hasPermission('system:notice:edit')")
    @Log(title = "通知公告", businessType = BusinessType.UPDATE)
    @PutMapping
    @Operation(summary = "修改通知公告")
    public ApiResult<Void> edit(@Validated @RequestBody NoticeSaveDTO request) {
        SysNotice notice = BeanUtil.toBean(request, SysNotice.class);
        return toApiResult(noticeService.updateNotice(notice));
    }

    @PreAuthorize("@se.hasPermission('system:notice:remove')")
    @Log(title = "通知公告", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    @Operation(summary = "删除通知公告")
    public ApiResult<Void> remove(@Parameter(description = "公告 ID 列表，多个用逗号分隔") @PathVariable Long[] ids) {
        return toApiResult(noticeService.deleteNoticeByIds(ids));
    }

    /**
     * 最新5条正常公告及当前用户全部正常公告的未读总数。
     */
    @GetMapping("/listTop")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "查询首页通知公告")
    public ApiResult<NoticeReadVO> listTop() {
        Long userId = SecurityUtils.getUserId();
        List<SysNotice> list = noticeReadService.selectNoticeListWithReadStatus(userId, 5);
        int unreadCount = noticeReadService.selectUnreadCount(userId);
        NoticeReadVO noticeReadVO = new NoticeReadVO(list, unreadCount);
        return ApiResult.success(noticeReadVO);
    }

    /**
     * 标记公告已读
     */
    @PostMapping("/markRead")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "将公告标为已读")
    public ApiResult<Void> markRead(@Parameter(description = "公告 ID") @RequestParam @Positive Long noticeId) {
        Long userId = SecurityUtils.getUserId();
        noticeReadService.markRead(noticeId, userId);
        return success();
    }
    /**
     * 批量标记已读
     */
    @PostMapping("/markReadAll")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "批量将公告标为已读")
    public ApiResult<Void> markReadAll(@Parameter(description = "公告 ID 列表，多个用逗号分隔") @RequestParam Long[] ids)
    {
        Long userId = SecurityUtils.getUserId();
        noticeReadService.markReadBatch(userId, ids);
        return success();
    }
    /**
     * 已读用户列表数据
     */
    @PreAuthorize("@se.hasPermission('system:notice:list')")
    @GetMapping("/readUsers/list")
    @Operation(summary = "分页查询公告阅读记录")
    public ApiResult<PageResult<NoticeReadUserVO>> readUsersList(@Validated @ParameterObject NoticeReadUserPageReqDTO query)
    {
        return success(noticeReadService.selectReadUsersPage(query));
    }

}
