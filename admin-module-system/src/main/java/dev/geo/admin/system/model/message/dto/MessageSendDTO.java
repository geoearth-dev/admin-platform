package dev.geo.admin.system.model.message.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 模板消息发送参数。
 */
@Getter
@Setter
@Schema(description = "模板消息发送参数")
public class MessageSendDTO {

    @NotBlank(message = "模板编码不能为空")
    @Size(max = 64, message = "模板编码不能超过64个字符")
    @Schema(description = "模板编码")
    private String templateCode;

    @NotNull(message = "接收人不能为空")
    @Schema(description = "接收人 ID")
    private Long receiverId;

    @Schema(description = "所属业务模块")
    private Integer module;
    @Schema(description = "业务类型")
    private Integer businessType;
    @Schema(description = "业务记录 ID")
    private Long businessId;

    @Size(max = 500, message = "业务链接不能超过500个字符")
    @Schema(description = "关联业务页面地址")
    private String businessUrl;

    /** 模板变量，例如 {"name":"张三"}。 */
    @NotNull(message = "模板变量不能为空")
    @Schema(description = "模板变量，键名需与模板中的占位符一致")
    private Map<String, Object> variables = new LinkedHashMap<>();
}
