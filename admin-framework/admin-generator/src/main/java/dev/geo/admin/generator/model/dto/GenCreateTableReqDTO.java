package dev.geo.admin.generator.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "建表参数")
public record GenCreateTableReqDTO(
        @Schema(description = "建表 SQL，仅支持 CREATE TABLE 语句")
        @NotBlank String sql
) {
}
