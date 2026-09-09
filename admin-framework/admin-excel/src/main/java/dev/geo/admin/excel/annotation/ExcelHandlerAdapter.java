package dev.geo.admin.excel.annotation;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Excel 单元格值扩展处理器。
 *
 * <p>实现类应保持无状态，Excel 模块会复用处理器实例。</p>
 */
public interface ExcelHandlerAdapter {

    /**
     * 格式化单元格值。
     *
     * @param value    当前值
     * @param args     注解参数
     * @param cell     当前单元格
     * @param workbook 当前工作簿
     * @return 格式化后的值
     */
    Object format(Object value, String[] args, Cell cell, Workbook workbook);
}
