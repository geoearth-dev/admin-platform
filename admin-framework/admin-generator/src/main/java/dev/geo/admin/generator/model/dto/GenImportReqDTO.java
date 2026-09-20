package dev.geo.admin.generator.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Schema(description = "导入表结构参数")
public record GenImportReqDTO(
        @Schema(description = "待导入的数据库表名列表")
        @NotEmpty List<@NotBlank String> tables
) {
}
