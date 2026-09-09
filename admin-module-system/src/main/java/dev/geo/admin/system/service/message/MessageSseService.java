package dev.geo.admin.system.service.message;

import dev.geo.admin.system.model.message.vo.MessagePushVO;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 维护当前实例上的消息推送连接。
 */
@Service
public class MessageSseService {
    private static final long TIMEOUT_MILLIS = 30 * 60 * 1000L;

    private final ConcurrentHashMap<Long, Set<SseEmitter>> connections = new ConcurrentHashMap<>();

    public SseEmitter connect(Long userId) {
        SseEmitter emitter = new SseEmitter(TIMEOUT_MILLIS);
        connections.computeIfAbsent(userId, ignored -> ConcurrentHashMap.newKeySet()).add(emitter);
        emitter.onCompletion(() -> remove(userId, emitter));
        emitter.onTimeout(() -> remove(userId, emitter));
        emitter.onError(exception -> remove(userId, emitter));
        return emitter;
    }

    public void pushUnreadCount(Long userId, long unreadCount) {
        Set<SseEmitter> emitters = connections.get(userId);
        if (emitters == null) {
            return;
        }
        MessagePushVO payload = MessagePushVO.unreadCount(unreadCount);
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name(payload.type()).data(payload));
            } catch (IOException exception) {
                emitter.complete();
                remove(userId, emitter);
            }
        }
    }

    private void remove(Long userId, SseEmitter emitter) {
        Set<SseEmitter> emitters = connections.get(userId);
        if (emitters == null) {
            return;
        }
        emitters.remove(emitter);
        if (emitters.isEmpty()) {
            connections.remove(userId, emitters);
        }
    }
}
