package dev.geoearth.admin.common.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JsonCodec {
    private final JsonMapper jsonMapper;

    public String toJson(Object value) {
        try {
            return jsonMapper.writeValueAsString(value);
        } catch (Exception exception) {
            throw new IllegalStateException("JSON序列化失败", exception);
        }
    }

    public <T> List<T> fromJsonList(String json, Class<T> elementType) {
        try {
            var listType = jsonMapper.getTypeFactory().constructCollectionType(List.class, elementType);
            return jsonMapper.readValue(json, listType);
        } catch (Exception exception) {
            throw new IllegalStateException("JSON反序列化失败", exception);
        }
    }
}
