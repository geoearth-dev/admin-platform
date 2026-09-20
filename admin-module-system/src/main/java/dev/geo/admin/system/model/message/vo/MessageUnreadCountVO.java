package dev.geo.admin.system.model.message.vo;
import io.swagger.v3.oas.annotations.media.Schema;


/**
 * 未读消息数量。
 */
@Schema(description = "未读消息统计")
public record MessageUnreadCountVO(
        @Schema(description = "未读消息数量")
        long count
) {
}
