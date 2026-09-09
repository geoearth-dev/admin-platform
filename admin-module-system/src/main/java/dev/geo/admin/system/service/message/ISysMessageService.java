package dev.geo.admin.system.service.message;

import dev.geo.admin.common.core.model.PageResult;
import dev.geo.admin.system.model.message.dto.MessagePageReqDTO;
import dev.geo.admin.system.model.message.dto.MessageReadAllDTO;
import dev.geo.admin.system.model.message.dto.MessageSendDTO;
import dev.geo.admin.system.model.message.entity.SysMessage;

import java.util.Collection;
import java.util.List;

/**
 * 站内消息服务。
 */
public interface ISysMessageService {
    PageResult<SysMessage> selectMessagePage(MessagePageReqDTO query, Long receiverId);

    List<SysMessage> selectMessageList(MessagePageReqDTO query, Long receiverId);

    SysMessage selectMessage(Long id, Long receiverId);

    long countUnread(Long receiverId);

    Long sendMessage(MessageSendDTO request, Long senderId);

    int markRead(Long id, Long receiverId);

    int markAllRead(Long receiverId, MessageReadAllDTO request);

    int deleteMessages(Collection<Long> ids, Long receiverId);
}
