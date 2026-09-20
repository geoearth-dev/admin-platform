package dev.geo.admin.system.service.system.impl;

import dev.geo.admin.common.core.model.PageResult;
import dev.geo.admin.system.mapper.system.SysNoticeMapper;
import dev.geo.admin.system.model.system.dto.NoticePageReqDTO;
import dev.geo.admin.system.model.system.entity.SysNotice;
import dev.geo.admin.system.service.system.ISysNoticeService;
import dev.geo.admin.common.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 公告 服务层实现
 * 
 */
@Service
public class SysNoticeServiceImpl implements ISysNoticeService
{
    @Override
    public PageResult<SysNotice> selectNoticePage(NoticePageReqDTO query) {
        return noticeMapper.selectPage(query);
    }

    @Autowired
    private SysNoticeMapper noticeMapper;

    /**
     * 查询公告信息
     * 
     * @param noticeId 公告ID
     * @return 公告信息
     */
    @Override
    public SysNotice selectNoticeById(Long noticeId)
    {
        return noticeMapper.selectNoticeById(noticeId);
    }

    /**
     * 查询公告列表
     * 
     * @param notice 公告信息
     * @return 公告集合
     */
    @Override
    public List<SysNotice> selectNoticeList(SysNotice notice)
    {
        return noticeMapper.selectNoticeList(notice);
    }

    /**
     * 新增公告
     * 
     * @param notice 公告信息
     * @return 结果
     */
    @Override
    public int insertNotice(SysNotice notice)
    {
        return noticeMapper.insertNotice(notice);
    }

    /**
     * 修改公告
     * 
     * @param notice 公告信息
     * @return 结果
     */
    @Override
    public int updateNotice(SysNotice notice)
    {
        return noticeMapper.updateNotice(notice);
    }

    /**
     * 删除公告对象
     * 
     * @param noticeId 公告ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteNoticeById(Long noticeId)
    {
        return deleteNoticeByIds(new Long[]{noticeId});
    }

    /**
     * 批量删除公告信息
     * 
     * @param noticeIds 需要删除的公告ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteNoticeByIds(Long[] noticeIds)
    {
        if (noticeIds == null || noticeIds.length == 0 ||
                java.util.Arrays.stream(noticeIds).anyMatch(id -> id == null || id <= 0)) {
            throw new ServiceException("请选择有效的公告ID");
        }
        // 逻辑删除公告，保留已读历史；查询侧统一排除已删除公告。
        return noticeMapper.deleteNoticeByIds(noticeIds);
    }
}
