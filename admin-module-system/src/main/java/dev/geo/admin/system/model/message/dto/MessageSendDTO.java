package dev.geo.admin.system.model.message.dto;

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
public class MessageSendDTO {

    @NotBlank(message = "模板编码不能为空")
    @Size(max = 64, message = "模板编码不能超过64个字符")
    private String templateCode;

    @NotNull(message = "接收人不能为空")
    private Long receiverId;

    private Integer module;
    private Integer businessType;
    private Long businessId;

    @Size(max = 500, message = "业务链接不能超过500个字符")
    private String businessUrl;

    /** 模板变量，例如 {"name":"张三"}。 */
    @NotNull(message = "模板变量不能为空")
    private Map<String, Object> variables = new LinkedHashMap<>();
}
