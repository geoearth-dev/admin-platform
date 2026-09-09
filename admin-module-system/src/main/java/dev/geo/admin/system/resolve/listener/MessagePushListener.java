package dev.geo.admin.system.resolve.listener;

import dev.geo.admin.system.model.message.event.MessageUnreadChangedEvent;
import dev.geo.admin.system.service.message.MessageSseService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 数据库事务提交后向在线用户推送最新未读数量。
 */
@Component
@RequiredArgsConstructor
public class MessagePushListener {
    private final MessageSseService messageSseService;

    @Async("messageTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(MessageUnreadChangedEvent event) {
        messageSseService.pushUnreadCount(event.receiverId(), event.unreadCount());
    }
}
