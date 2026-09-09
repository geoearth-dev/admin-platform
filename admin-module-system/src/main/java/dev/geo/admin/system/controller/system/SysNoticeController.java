package dev.geo.admin.system.controller.system;

import cn.hutool.core.bean.BeanUtil;
import dev.geo.admin.common.annotation.Log;
import dev.geo.admin.common.core.controller.BaseController;
import dev.geo.admin.common.core.model.ApiResult;
import dev.geo.admin.common.enums.BusinessType;
import dev.geo.admin.common.core.model.PageResult;
import dev.geo.admin.system.model.system.dto.NoticePageReqDTO;
import dev.geo.admin.system.model.system.dto.NoticeSaveDTO;
import dev.geo.admin.system.model.system.entity.SysNotice;
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
public class SysNoticeController extends BaseController {
    private final ISysNoticeService noticeService;

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
}
