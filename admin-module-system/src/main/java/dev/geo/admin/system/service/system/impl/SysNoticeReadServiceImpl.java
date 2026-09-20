package dev.geo.admin.system.service.system.impl;

import dev.geo.admin.system.mapper.system.SysNoticeReadMapper;
import dev.geo.admin.system.mapper.system.SysNoticeMapper;
import dev.geo.admin.common.core.model.PageResult;
import dev.geo.admin.common.exception.ServiceException;
import dev.geo.admin.mybatis.model.converter.PageResultConverter;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import dev.geo.admin.system.model.system.dto.NoticeReadUserPageReqDTO;
import dev.geo.admin.system.model.system.vo.NoticeReadUserVO;
import dev.geo.admin.system.model.system.entity.SysNotice;
import dev.geo.admin.system.model.system.entity.SysNoticeRead;
import dev.geo.admin.system.service.system.ISysNoticeReadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Arrays;

/**
 * 公告已读记录 服务层实现
 *
 */
@Service
@RequiredArgsConstructor
public class SysNoticeReadServiceImpl implements ISysNoticeReadService
{
    private final SysNoticeReadMapper noticeReadMapper;
    private final SysNoticeMapper noticeMapper;

    /**
     * 标记已读
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markRead(Long noticeId, Long userId)
    {
        validateReadableNotices(new Long[]{noticeId});
        SysNoticeRead record = new SysNoticeRead();
        record.setNoticeId(noticeId);
        record.setUserId(userId);
        noticeReadMapper.insertNoticeRead(record);
    }

    /**
     * 查询某用户未读公告数量
     */
    @Override
    public int selectUnreadCount(Long userId)
    {
        return noticeReadMapper.selectUnreadCount(userId);
    }

    /**
     * 查询公告列表并标记当前用户已读状态
     */
    @Override
    public List<SysNotice> selectNoticeListWithReadStatus(Long userId, int limit)
    {
        return noticeReadMapper.selectNoticeListWithReadStatus(userId, limit);
    }

    /**
     * 批量标记已读
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markReadBatch(Long userId, Long[] noticeIds)
    {
        Long[] ids = validateReadableNotices(noticeIds);
        noticeReadMapper.insertNoticeReadBatch(userId, ids);
    }

    /**
     * 查询已阅读某公告的用户列表
     */
    @Override
    public PageResult<NoticeReadUserVO> selectReadUsersPage(NoticeReadUserPageReqDTO query)
    {
        if (noticeMapper.selectById(query.getNoticeId()) == null) {
            throw new ServiceException("公告不存在或已被删除");
        }
        return PageResultConverter.of(noticeReadMapper.selectReadUsersPage(
                Page.of(query.getPageNum(), query.getPageSize()), query));
    }

    /** 公告面向全部登录用户；关闭或不存在的公告不能标记已读。 */
    private Long[] validateReadableNotices(Long[] noticeIds) {
        if (noticeIds == null || noticeIds.length == 0 ||
                Arrays.stream(noticeIds).anyMatch(id -> id == null || id <= 0)) {
            throw new ServiceException("请选择有效的公告ID");
        }
        Long[] ids = Arrays.stream(noticeIds).distinct().toArray(Long[]::new);
        List<SysNotice> notices = noticeMapper.selectByIds(Arrays.asList(ids));
        if (notices.size() != ids.length) {
            throw new ServiceException("部分公告不存在或已被删除");
        }
        if (notices.stream().anyMatch(notice -> !"1".equals(notice.getStatus()))) {
            throw new ServiceException("关闭的公告不能标记已读");
        }
        return ids;
    }
}
