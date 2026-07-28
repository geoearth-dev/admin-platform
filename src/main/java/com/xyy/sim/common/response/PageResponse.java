package com.xyy.sim.common.response;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Schema(description = "分页响应")
public class PageResponse<T> {
    @Schema(description = "当前页数据")
    private List<T> records;

    @Schema(description = "总记录数", example = "100")
    private long total;

    @Schema(description = "当前页码", example = "1")
    private long pageNum;

    @Schema(description = "每页数量", example = "10")
    private long pageSize;

    @Schema(description = "总页数", example = "10")
    private long pages;

    public static <T> PageResponse<T> of(IPage<T> page) {
        return new PageResponse<>(
                page.getRecords(),
                page.getTotal(),
                page.getCurrent(),
                page.getSize(),
                page.getPages());
    }
}