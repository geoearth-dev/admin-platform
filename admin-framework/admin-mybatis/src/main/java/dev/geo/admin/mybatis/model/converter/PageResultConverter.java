package dev.geo.admin.mybatis.model.converter;

import com.baomidou.mybatisplus.core.metadata.IPage;
import dev.geo.admin.common.core.model.PageResult;

public final class PageResultConverter {
    /**
     * 将 MyBatis-Plus 分页结果转换为通用分页结果。
     */
    public static <T> PageResult<T> of(IPage<T> page) {
        return PageResult.<T>builder()
                .records(page.getRecords())
                .total(page.getTotal())
                .pageNum(page.getCurrent())
                .pageSize(page.getSize())
                .pages(page.getPages())
                .build();

    }
}
