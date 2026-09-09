package dev.geo.admin.common.core.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.function.Function;

@Builder
@AllArgsConstructor
@Schema(description = "分页响应")
@Getter
public class PageResult<T> {

    @Schema(description = "当前页数据")
    private List<T> records;

    @Schema(description = "总记录数", example = "100")
    private long total;

    @Schema(description = "当前页码", example = "1")
    private long pageNum;

    @Schema(description = "每页数量", example = "20")
    private long pageSize;

    @Schema(description = "总页数", example = "5")
    private long pages;

    public <R> PageResult<R> convert(Function<T, R> converter) {
        List<R> convertedRecords = records.stream().map(converter).toList();

        return new PageResult<>(convertedRecords, total, pageNum, pageSize, pages);
    }
}
