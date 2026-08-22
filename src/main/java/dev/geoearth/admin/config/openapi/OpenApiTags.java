package dev.geoearth.admin.config.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.tags.Tag;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * OpenAPI 标签统一定义。
 *
 * <p>标签名称、说明和 Scalar 显示顺序只在本类维护，Controller 和 OpenAPI
 * 配置均引用此处，避免多处文字不一致。</p>
 */
public final class OpenApiTags {

    public static final String SYS_USER = "用户信息";
    public static final String SYS_USER_DESCRIPTION = "维护用户信息";

    public static final String SYS_OPERATION_LOG = "操作日志";
    public static final String SYS_OPERATION_LOG_DESCRIPTION = "维护操作日志";

    public static final String SYS_LOGIN_LOG = "登录日志";
    public static final String SYS_LOGIN_LOG_DESCRIPTION = "维护登录日志";

    public static final String SYS_DICT = "系统字典";
    public static final String SYS_DICT_DESCRIPTION = "维护系统业务字典类型和字典数据";

    private static final List<TagDefinition> ORDERED_TAGS = List.of(
            tag(SYS_USER, SYS_USER_DESCRIPTION),
            tag(SYS_OPERATION_LOG, SYS_OPERATION_LOG_DESCRIPTION),
            tag(SYS_LOGIN_LOG, SYS_LOGIN_LOG_DESCRIPTION),
            tag(SYS_DICT, SYS_DICT_DESCRIPTION)
    );

    /**
     * 使用统一定义覆盖已注册标签，并将未登记的扩展标签追加在末尾。
     */
    public static void applyTo(OpenAPI openApi) {
        Map<String, Tag> tagsByName = new LinkedHashMap<>();
        for (TagDefinition definition : ORDERED_TAGS) {
            tagsByName.put(definition.name(), new io.swagger.v3.oas.models.tags.Tag()
                    .name(definition.name())
                    .description(definition.description()));
        }

        if (openApi.getTags() != null) {
            for (io.swagger.v3.oas.models.tags.Tag tag : openApi.getTags()) {
                tagsByName.putIfAbsent(tag.getName(), tag);
            }
        }
        openApi.setTags(new ArrayList<>(tagsByName.values()));
    }

    private static TagDefinition tag(String name, String description) {
        return new TagDefinition(name, description);
    }

    private record TagDefinition(String name, String description) {
    }
}
