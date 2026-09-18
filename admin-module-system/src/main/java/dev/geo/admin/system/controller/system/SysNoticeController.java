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
import jakarta.validation.constraints.Positive;
import dev.geo.admin.system.service.system.ISysNoticeReadService;
import dev.geo.admin.system.service.system.ISysNoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 通知公告管理接口。
 */
@RestController
@RequestMapping("/system/notice")
@RequiredArgsConstructor
@Validated
public class SysNoticeController extends BaseController {
    private final ISysNoticeService noticeService;
    private final ISysNoticeReadService noticeReadService;

    @PreAuthorize("@se.hasPermission('system:notice:list')")
    @GetMapping("/list")
    public ApiResult<PageResult<SysNotice>> list(@Validated NoticePageReqDTO query) {
        return success(noticeService.selectNoticePage(query));
    }

    @PreAuthorize("@se.hasPermission('system:notice:query')")
    @GetMapping("/{id}")
    public ApiResult<SysNotice> getInfo(@PathVariable Long id) {
        return success(noticeService.selectNoticeById(id));
    }

    @PreAuthorize("@se.hasPermission('system:notice:add')")
    @Log(title = "通知公告", businessType = BusinessType.INSERT)
    @PostMapping
    public ApiResult<Void> add(@Validated @RequestBody NoticeSaveDTO request) {
        SysNotice notice = BeanUtil.toBean(request, SysNotice.class);
        return toApiResult(noticeService.insertNotice(notice));
    }

    @PreAuthorize("@se.hasPermission('system:notice:edit')")
    @Log(title = "通知公告", businessType = BusinessType.UPDATE)
    @PutMapping
    public ApiResult<Void> edit(@Validated @RequestBody NoticeSaveDTO request) {
        SysNotice notice = BeanUtil.toBean(request, SysNotice.class);
        return toApiResult(noticeService.updateNotice(notice));
    }

    @PreAuthorize("@se.hasPermission('system:notice:remove')")
    @Log(title = "通知公告", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public ApiResult<Void> remove(@PathVariable Long[] ids) {
        return toApiResult(noticeService.deleteNoticeByIds(ids));
    }

    /**
     * 最新5条正常公告及当前用户全部正常公告的未读总数。
     */
    @GetMapping("/listTop")
    @PreAuthorize("isAuthenticated()")
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
    public ApiResult<Void> markRead(@RequestParam @Positive Long noticeId) {
        Long userId = SecurityUtils.getUserId();
        noticeReadService.markRead(noticeId, userId);
        return success();
    }
    /**
     * 批量标记已读
     */
    @PostMapping("/markReadAll")
    @PreAuthorize("isAuthenticated()")
    public ApiResult<Void> markReadAll(@RequestParam Long[] ids)
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
    public ApiResult<PageResult<NoticeReadUserVO>> readUsersList(@Validated NoticeReadUserPageReqDTO query)
    {
        return success(noticeReadService.selectReadUsersPage(query));
    }

}
