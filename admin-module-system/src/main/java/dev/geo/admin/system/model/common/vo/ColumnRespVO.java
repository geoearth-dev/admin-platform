package dev.geo.admin.system.model.common.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ColumnRespVO {

    @Schema(description = "Excel文件路径")
    private String csvFile;

    @Schema(description = "字段")
    private List<String> columnList;
}
