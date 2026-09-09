package dev.geo.admin.system.service.message.impl;

import dev.geo.admin.common.core.model.PageResult;
import dev.geo.admin.common.exception.ServiceException;
import dev.geo.admin.system.mapper.message.SysMessageMapper;
import dev.geo.admin.system.mapper.message.SysMessageTemplateMapper;
import dev.geo.admin.system.model.message.dto.MessagePageReqDTO;
import dev.geo.admin.system.model.message.dto.MessageReadAllDTO;
import dev.geo.admin.system.model.message.dto.MessageSendDTO;
import dev.geo.admin.system.model.message.entity.SysMessage;
import dev.geo.admin.system.model.message.entity.SysMessageTemplate;
import dev.geo.admin.system.model.message.enums.MessageReadStatus;
import dev.geo.admin.system.model.message.event.MessageUnreadChangedEvent;
import dev.geo.admin.system.service.message.ISysMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 站内消息服务实现。
 */
@Service
@RequiredArgsConstructor
public class SysMessageServiceImpl implements ISysMessageService {
    private static final String ENABLED = "0";

    private final SysMessageMapper messageMapper;
    private final SysMessageTemplateMapper templateMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public PageResult<SysMessage> selectMessagePage(MessagePageReqDTO query, Long receiverId) {
        return messageMapper.selectMessagePage(query, receiverId);
    }

    @Override
    public List<SysMessage> selectMessageList(MessagePageReqDTO query, Long receiverId) {
        return messageMapper.selectMessageList(query, receiverId);
    }

    @Override
    public SysMessage selectMessage(Long id, Long receiverId) {
        SysMessage message = messageMapper.selectOwnedMessage(id, receiverId);
        if (message == null) {
            throw new ServiceException("消息不存在");
        }
        return message;
    }

    @Override
    public long countUnread(Long receiverId) {
        return messageMapper.selectUnreadCount(receiverId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long sendMessage(MessageSendDTO request, Long senderId) {
        SysMessageTemplate template = templateMapper.selectByTemplateCode(request.getTemplateCode());
        if (template == null || !ENABLED.equals(template.getStatus())) {
            throw new ServiceException("消息模板不存在或已停用");
        }

        SysMessage message = new SysMessage();
        message.setSenderId(senderId);
        message.setReceiverId(request.getReceiverId());
        message.setTitle(render(template.getTitleTemplate(), request.getVariables()));
        message.setContent(render(template.getContentTemplate(), request.getVariables()));
        message.setCategory(template.getCategory());
        message.setMessageLevel(template.getMessageLevel());
        message.setModule(request.getModule());
        message.setBusinessType(request.getBusinessType());
        message.setBusinessId(request.getBusinessId());
        message.setBusinessUrl(request.getBusinessUrl());
        message.setReadStatus(MessageReadStatus.UNREAD.getCode());
        message.setDelFlag(0);
        messageMapper.insert(message);

        publishUnreadCount(request.getReceiverId());
        return message.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int markRead(Long id, Long receiverId) {
        SysMessage message = selectMessage(id, receiverId);
        if (MessageReadStatus.READ.getCode() == message.getReadStatus()) {
            return 1;
        }
        int rows = messageMapper.markRead(id, receiverId, Instant.now());
        publishUnreadCount(receiverId);
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int markAllRead(Long receiverId, MessageReadAllDTO request) {
        int rows = messageMapper.markAllRead(receiverId, request.getCategory(), request.getModule(), Instant.now());
        publishUnreadCount(receiverId);
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteMessages(Collection<Long> ids, Long receiverId) {
        int rows = messageMapper.deleteOwnedMessages(ids, receiverId);
        publishUnreadCount(receiverId);
        return rows;
    }

    /**
     * 使用请求变量替换模板中的 ${name} 占位符。
     */
    private String render(String template, Map<String, Object> variables) {
        String result = template;
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            String value = entry.getValue() == null ? "" : String.valueOf(entry.getValue());
            result = result.replace("${" + entry.getKey() + "}", value);
        }
        return result;
    }

    private void publishUnreadCount(Long receiverId) {
        long unreadCount = messageMapper.selectUnreadCount(receiverId);
        eventPublisher.publishEvent(new MessageUnreadChangedEvent(receiverId, unreadCount));
    }
}
