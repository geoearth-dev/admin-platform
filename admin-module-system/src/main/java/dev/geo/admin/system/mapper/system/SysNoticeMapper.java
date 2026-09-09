package dev.geo.admin.system.mapper.system;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import dev.geo.admin.common.core.model.PageResult;
import dev.geo.admin.mybatis.mapper.BaseMapperX;
import dev.geo.admin.system.model.system.dto.NoticePageReqDTO;
import dev.geo.admin.system.model.system.entity.SysNotice;

import java.util.List;

/**
 * 通知公告表 数据层
 *
 */
public interface SysNoticeMapper extends BaseMapperX<SysNotice>
{
    default PageResult<SysNotice> selectPage(NoticePageReqDTO query) {
        return selectPage(query, new LambdaQueryWrapper<SysNotice>()
                .like(StrUtil.isNotBlank(query.getNoticeTitle()), SysNotice::getNoticeTitle, query.getNoticeTitle())
                .eq(StrUtil.isNotBlank(query.getNoticeType()), SysNotice::getNoticeType, query.getNoticeType())
                .eq(StrUtil.isNotBlank(query.getStatus()), SysNotice::getStatus, query.getStatus())
                .like(StrUtil.isNotBlank(query.getCreateBy()), SysNotice::getCreateBy, query.getCreateBy())
                .orderByDesc(SysNotice::getCreateTime)
                .orderByDesc(SysNotice::getId));
    }
    /**
     * 查询公告信息
     * 
     * @param noticeId 公告ID
     * @return 公告信息
     */
    default SysNotice selectNoticeById(Long id) {
        return selectById(id);
    }

    /**
     * 查询公告列表
     * 
     * @param notice 公告信息
     * @return 公告集合
     */
    default List<SysNotice> selectNoticeList(SysNotice query) {
        return selectList(new LambdaQueryWrapper<SysNotice>()
                .like(StrUtil.isNotBlank(query.getNoticeTitle()), SysNotice::getNoticeTitle, query.getNoticeTitle())
                .eq(StrUtil.isNotBlank(query.getNoticeType()), SysNotice::getNoticeType, query.getNoticeType())
                .eq(StrUtil.isNotBlank(query.getStatus()), SysNotice::getStatus, query.getStatus())
                .like(StrUtil.isNotBlank(query.getCreateBy()), SysNotice::getCreateBy, query.getCreateBy())
                .orderByDesc(SysNotice::getCreateTime)
                .orderByDesc(SysNotice::getId));
    }

    /**
     * 新增公告
     * 
     * @param notice 公告信息
     * @return 结果
     */
    default int insertNotice(SysNotice notice) {
        return insert(notice);
    }

    /**
     * 修改公告
     * 
     * @param notice 公告信息
     * @return 结果
     */
    default int updateNotice(SysNotice notice) {
        return updateById(notice);
    }

    /**
     * 批量删除公告
     * 
     * @param noticeId 公告ID
     * @return 结果
     */
    default int deleteNoticeById(Long id) {
        return deleteById(id);
    }

    /**
     * 批量删除公告信息
     * 
     * @param noticeIds 需要删除的公告ID
     * @return 结果
     */
    default int deleteNoticeByIds(Long[] ids) {
        return deleteByIds(List.of(ids));
    }
}
