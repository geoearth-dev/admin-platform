package dev.geo.admin.system.model.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;


@Data
public class CsvColumnReqDTO {

    @Schema(description = "csv文件路径")
    private String file;
}
