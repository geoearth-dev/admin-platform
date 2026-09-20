package dev.geo.admin.system.model.message.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 批量标记消息已读的筛选条件。
 */
@Getter
@Setter
@Schema(description = "消息已读范围")
public class MessageReadAllDTO {
    @Schema(description = "消息分类，不传则不限分类")
    private Integer category;
    @Schema(description = "业务模块，不传则不限模块")
    private Integer module;
}
