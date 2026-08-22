package dev.geoearth.admin.model.vo.dictionary;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * 前端下拉框字典选项。
 */
@Getter
@Builder
@Schema(description = "字典键值")
public class DictOptionVO {
    @Schema(description = "字典值", example = "1")
    private String value;

    @Schema(description = "字典标签", example = "海战")
    private String label;
}