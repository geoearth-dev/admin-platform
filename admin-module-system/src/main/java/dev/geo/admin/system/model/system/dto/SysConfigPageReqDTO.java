package dev.geo.admin.system.model.system.dto;

import dev.geo.admin.mybatis.model.page.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * 参数配置分页查询条件。
 *
 * <p>查询模型与数据库实体分离，避免查询接口复用新增、修改场景的校验规则。</p>
 */
@Getter
@Setter
@Schema(description = "参数配置分页查询条件")
public class SysConfigPageReqDTO extends PageParam {

    @Schema(description = "参数名称", example = "验证码开关")
    private String configName;

    @Schema(description = "参数键名", example = "sys.account.captchaEnabled")
    private String configKey;

    @Schema(description = "系统内置：Y-是，N-否", example = "Y")
    private String configType;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Schema(description = "创建日期起始值", example = "2026-01-01")
    private LocalDate beginDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Schema(description = "创建日期结束值", example = "2026-12-31")
    private LocalDate endDate;
}
