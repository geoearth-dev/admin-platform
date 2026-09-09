package dev.geo.admin.system.model.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;


@Data
public class ExcelColumnReqDTO {

    @Schema(description = "Excel文件路径")
    private String excelFile;

    @Schema(description = "列开始行")
    private Integer startColumn;

    @Schema(description = "数据开始行")
    private Integer startData;

    @Schema(description = "Task Type")
    private String taskType;
}
