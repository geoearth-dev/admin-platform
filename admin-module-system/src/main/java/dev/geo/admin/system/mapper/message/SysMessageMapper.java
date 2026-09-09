package dev.geo.admin.system.mapper.message;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import dev.geo.admin.common.core.model.PageResult;
import dev.geo.admin.mybatis.mapper.BaseMapperX;
import dev.geo.admin.system.model.message.dto.MessagePageReqDTO;
import dev.geo.admin.system.model.message.entity.SysMessage;
import dev.geo.admin.system.model.message.enums.MessageReadStatus;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

/**
 * 站内消息数据访问接口。
 */
public interface SysMessageMapper extends BaseMapperX<SysMessage> {

    default PageResult<SysMessage> selectMessagePage(MessagePageReqDTO query, Long receiverId) {
        return selectPage(query, buildQuery(query, receiverId));
    }

    default List<SysMessage> selectMessageList(MessagePageReqDTO query, Long receiverId) {
        return selectList(buildQuery(query, receiverId));
    }

    default SysMessage selectOwnedMessage(Long id, Long receiverId) {
        return selectOne(new LambdaQueryWrapper<SysMessage>()
                .eq(SysMessage::getId, id)
                .eq(SysMessage::getReceiverId, receiverId));
    }

    default long selectUnreadCount(Long receiverId) {
        return selectCount(new LambdaQueryWrapper<SysMessage>()
                .eq(SysMessage::getReceiverId, receiverId)
                .eq(SysMessage::getReadStatus, MessageReadStatus.UNREAD.getCode()));
    }

    default int markRead(Long id, Long receiverId, Instant readTime) {
        SysMessage update = new SysMessage();
        update.setReadStatus(MessageReadStatus.READ.getCode());
        update.setReadTime(readTime);
        return update(update, new LambdaUpdateWrapper<SysMessage>()
                .eq(SysMessage::getId, id)
                .eq(SysMessage::getReceiverId, receiverId)
                .eq(SysMessage::getReadStatus, MessageReadStatus.UNREAD.getCode()));
    }

    default int markAllRead(Long receiverId, Integer category, Integer module, Instant readTime) {
        SysMessage update = new SysMessage();
        update.setReadStatus(MessageReadStatus.READ.getCode());
        update.setReadTime(readTime);
        return update(update, new LambdaUpdateWrapper<SysMessage>()
                .eq(SysMessage::getReceiverId, receiverId)
                .eq(SysMessage::getReadStatus, MessageReadStatus.UNREAD.getCode())
                .eq(category != null, SysMessage::getCategory, category)
                .eq(module != null, SysMessage::getModule, module));
    }

    default int deleteOwnedMessages(Collection<Long> ids, Long receiverId) {
        SysMessage update = new SysMessage();
        update.setDelFlag(1);
        return update(update, new LambdaUpdateWrapper<SysMessage>()
                .eq(SysMessage::getReceiverId, receiverId)
                .in(SysMessage::getId, ids));
    }

    private LambdaQueryWrapper<SysMessage> buildQuery(MessagePageReqDTO query, Long receiverId) {
        return new LambdaQueryWrapper<SysMessage>()
                .eq(SysMessage::getReceiverId, receiverId)
                .like(StrUtil.isNotBlank(query.getTitle()), SysMessage::getTitle, query.getTitle())
                .eq(query.getCategory() != null, SysMessage::getCategory, query.getCategory())
                .eq(query.getMessageLevel() != null, SysMessage::getMessageLevel, query.getMessageLevel())
                .eq(query.getModule() != null, SysMessage::getModule, query.getModule())
                .eq(query.getReadStatus() != null, SysMessage::getReadStatus, query.getReadStatus())
                .ge(query.getBeginTime() != null, SysMessage::getCreateTime, query.getBeginTime())
                .lt(query.getEndTime() != null, SysMessage::getCreateTime, query.getEndTime())
                .orderByDesc(SysMessage::getCreateTime)
                .orderByDesc(SysMessage::getId);
    }
}
