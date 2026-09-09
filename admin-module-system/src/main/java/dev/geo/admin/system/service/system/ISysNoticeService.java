package dev.geo.admin.system.service.system;

import dev.geo.admin.common.core.model.PageResult;
import dev.geo.admin.system.model.system.dto.NoticePageReqDTO;
import dev.geo.admin.system.model.system.entity.SysNotice;

import java.util.List;

/**
 * 公告 服务层
 *
 */
public interface ISysNoticeService
{
    PageResult<SysNotice> selectNoticePage(NoticePageReqDTO query);

    /**
     * 查询公告信息
     * 
     * @param noticeId 公告ID
     * @return 公告信息
     */
    SysNotice selectNoticeById(Long noticeId);

    /**
     * 查询公告列表
     * 
     * @param notice 公告信息
     * @return 公告集合
     */
    List<SysNotice> selectNoticeList(SysNotice notice);

    /**
     * 新增公告
     * 
     * @param notice 公告信息
     * @return 结果
     */
    int insertNotice(SysNotice notice);

    /**
     * 修改公告
     * 
     * @param notice 公告信息
     * @return 结果
     */
    int updateNotice(SysNotice notice);

    /**
     * 删除公告信息
     * 
     * @param noticeId 公告ID
     * @return 结果
     */
    int deleteNoticeById(Long noticeId);

    /**
     * 批量删除公告信息
     * 
     * @param noticeIds 需要删除的公告ID
     * @return 结果
     */
    int deleteNoticeByIds(Long[] noticeIds);
}
