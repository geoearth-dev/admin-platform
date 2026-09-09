package dev.geo.admin.excel.core;

import dev.geo.admin.excel.annotation.Excel;
import dev.geo.admin.excel.dict.ExcelDictResolver;
import dev.geo.admin.excel.support.ExcelField;
import dev.geo.admin.excel.support.ExcelMetadataResolver;
import dev.geo.admin.excel.support.ExcelValueConverter;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Excel 导入导出统一入口。
 *
 * <p>该组件本身不保存工作簿、Sheet、行号等请求级状态，可以安全地作为 Spring 单例使用。</p>
 * <p>导出使用 SXSSFWorkbook 降低大数据量导出时的内存占用；导入使用 WorkbookFactory
 * 自动识别 xls 与 xlsx。</p>
 */
@Component
public class ExcelService {

    private static final String XLSX_CONTENT_TYPE =
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final int STREAM_WINDOW_SIZE = 500;
    private static final int MAX_IMPORT_ROWS = 200_000;

    private final ExcelDictResolver dictResolver;
    private final AutowireCapableBeanFactory beanFactory;

    public ExcelService(
            ObjectProvider<ExcelDictResolver> resolverProvider,
            AutowireCapableBeanFactory beanFactory
    ) {
        this.dictResolver = resolverProvider.orderedStream()
                .findFirst()
                .orElseGet(MissingDictResolver::new);
        this.beanFactory = beanFactory;
    }

    /**
     * 将单个 Sheet 直接写入 HTTP 响应。
     *
     * <p>下载文件名由前端下载组件指定，{@code sheetName} 仅表示工作表名称。</p>
     */
    public <T> void exportExcel(
            HttpServletResponse response,
            List<T> rows,
            Class<T> rowType,
            String sheetName
    ) {
        Objects.requireNonNull(response, "HTTP 响应不能为空");
        response.setContentType(XLSX_CONTENT_TYPE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        try {
            exportExcel(response.getOutputStream(), rows, rowType, sheetName);
        } catch (IOException exception) {
            throw new ExcelException("获取 Excel 响应输出流失败", exception);
        }
    }

    /**
     * 导出单个 Sheet。
     */
    public <T> void exportExcel(
            OutputStream outputStream,
            List<T> rows,
            Class<T> rowType,
            String sheetName
    ) {
        exportExcel(outputStream, rows, rowType, sheetName, "", ExcelOptions.DEFAULT);
    }

    /**
     * 导出单个 Sheet，并支持标题和字段过滤。
     */
    public <T> void exportExcel(
            OutputStream outputStream,
            List<T> rows,
            Class<T> rowType,
            String sheetName,
            String title,
            ExcelOptions options
    ) {
        Objects.requireNonNull(outputStream, "Excel 输出流不能为空");
        ExcelSheet<T> descriptor = new ExcelSheet<>(sheetName, rows, rowType, title);
        exportMultiSheet(outputStream, List.of(descriptor), options);
    }

    /**
     * 导出导入模板。模板只写表头、提示和下拉选项，不写业务数据。
     */
    public <T> void exportTemplate(
            OutputStream outputStream,
            Class<T> rowType,
            String sheetName,
            String title
    ) {
        Objects.requireNonNull(outputStream, "Excel 输出流不能为空");
        SXSSFWorkbook workbook = new SXSSFWorkbook(STREAM_WINDOW_SIZE);
        try (workbook) {
            ExcelValueConverter converter = new ExcelValueConverter(dictResolver, beanFactory);
            writeSheet(
                    workbook,
                    new ExcelSheet<>(sheetName, List.of(), rowType, title),
                    ExcelOptions.DEFAULT,
                    converter,
                    true
            );
            workbook.write(outputStream);
            outputStream.flush();
        } catch (IOException exception) {
            throw new ExcelException("导出 Excel 模板失败", exception);
        }
    }

    /**
     * 将不同类型的数据导出到同一个工作簿的多个 Sheet。
     */
    public void exportMultiSheet(OutputStream outputStream, List<ExcelSheet<?>> sheets) {
        exportMultiSheet(outputStream, sheets, ExcelOptions.DEFAULT);
    }

    /**
     * 将不同类型的数据导出到同一个工作簿的多个 Sheet。
     */
    public void exportMultiSheet(
            OutputStream outputStream,
            List<ExcelSheet<?>> sheets,
            ExcelOptions options
    ) {
        Objects.requireNonNull(outputStream, "Excel 输出流不能为空");
        if (sheets == null || sheets.isEmpty()) {
            throw new IllegalArgumentException("至少需要一个 Excel Sheet");
        }

        Set<String> names = new HashSet<>();
        for (ExcelSheet<?> sheet : sheets) {
            String safeName = WorkbookUtil.createSafeSheetName(sheet.sheetName());
            if (!names.add(safeName)) {
                throw new IllegalArgumentException("Excel Sheet 名称重复：" + safeName);
            }
        }

        SXSSFWorkbook workbook = new SXSSFWorkbook(STREAM_WINDOW_SIZE);
        try (workbook) {
            ExcelValueConverter converter = new ExcelValueConverter(dictResolver, beanFactory);
            for (ExcelSheet<?> descriptor : sheets) {
                writeUnknownSheet(workbook, descriptor, options, converter);
            }
            workbook.write(outputStream);
            outputStream.flush();
        } catch (IOException exception) {
            throw new ExcelException("导出多 Sheet Excel 失败", exception);
        }
    }

    /**
     * 从第一个 Sheet 导入数据，默认第一行为表头。
     */
    public <T> List<T> importExcel(InputStream inputStream, Class<T> rowType) {
        return importExcel(inputStream, rowType, null, 0, ExcelOptions.DEFAULT);
    }

    /**
     * 从指定 Sheet 导入数据。
     *
     * @param sheetName Sheet 名称；为空时读取第一个 Sheet
     * @param headerRowIndex 表头行索引，从 0 开始
     */
    public <T> List<T> importExcel(
            InputStream inputStream,
            Class<T> rowType,
            String sheetName,
            int headerRowIndex,
            ExcelOptions options
    ) {
        Objects.requireNonNull(inputStream, "Excel 输入流不能为空");
        Objects.requireNonNull(rowType, "Excel 数据类型不能为空");
        if (headerRowIndex < 0) {
            throw new IllegalArgumentException("Excel 表头行索引不能小于 0");
        }

        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = sheetName == null || sheetName.isBlank()
                    ? workbook.getSheetAt(0)
                    : workbook.getSheet(sheetName);

            if (sheet == null) {
                throw new ExcelException("Excel Sheet 不存在：" + sheetName);
            }
            if (sheet.getLastRowNum() - headerRowIndex > MAX_IMPORT_ROWS) {
                throw new ExcelException("Excel 导入行数不能超过 " + MAX_IMPORT_ROWS);
            }

            List<ExcelField> fields = ExcelMetadataResolver.resolve(rowType, Excel.Type.IMPORT, options);
            if (fields.isEmpty()) {
                throw new ExcelException("类型未声明可导入的 @Excel 字段：" + rowType.getName());
            }

            Row header = sheet.getRow(headerRowIndex);
            if (header == null) {
                throw new ExcelException("Excel 表头行为空");
            }

            DataFormatter formatter = new DataFormatter();
            Map<Integer, ExcelField> columns = matchColumns(header, fields, formatter);
            ExcelValueConverter converter = new ExcelValueConverter(dictResolver, beanFactory);
            Constructor<T> constructor = rowType.getDeclaredConstructor();
            constructor.trySetAccessible();

            List<T> result = new ArrayList<>();
            for (int rowIndex = headerRowIndex + 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (isEmptyRow(row, formatter)) {
                    continue;
                }

                T entity = constructor.newInstance();
                for (Map.Entry<Integer, ExcelField> entry : columns.entrySet()) {
                    Cell cell = row.getCell(entry.getKey());
                    Object value = readCellValue(cell, formatter);
                    converter.writeProperty(entity, entry.getValue(), value, cell, workbook);
                }
                result.add(entity);
            }
            return List.copyOf(result);
        } catch (ExcelException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new ExcelException("导入 Excel 失败", exception);
        }
    }

    private <T> void writeSheet(
            SXSSFWorkbook workbook,
            ExcelSheet<T> descriptor,
            ExcelOptions options,
            ExcelValueConverter converter,
            boolean template
    ) {
        List<ExcelField> fields = ExcelMetadataResolver.resolve(
                descriptor.rowType(),
                template ? Excel.Type.IMPORT : Excel.Type.EXPORT,
                options
        );
        if (fields.isEmpty()) {
            throw new ExcelException("类型未声明可处理的 @Excel 字段：" + descriptor.rowType().getName());
        }

        String safeName = WorkbookUtil.createSafeSheetName(descriptor.sheetName());
        Sheet sheet = workbook.createSheet(safeName);
        Map<String, CellStyle> styles = createStyles(workbook);
        int rowIndex = 0;

        if (!descriptor.title().isBlank()) {
            Row titleRow = sheet.createRow(rowIndex++);
            titleRow.setHeightInPoints(30);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue(descriptor.title());
            titleCell.setCellStyle(styles.get("title"));
            if (fields.size() > 1) {
                sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, fields.size() - 1));
            }
        }

        int headerRowIndex = rowIndex;
        Row header = sheet.createRow(rowIndex++);
        header.setHeightInPoints(24);
        for (int column = 0; column < fields.size(); column++) {
            Excel annotation = fields.get(column).annotation();
            Cell cell = header.createCell(column);
            cell.setCellValue(annotation.name());
            cell.setCellStyle(headerStyle(workbook, styles, annotation));
            sheet.setColumnWidth(column, safeColumnWidth(annotation.width()));
        }

        Map<Integer, BigDecimal> statistics = new LinkedHashMap<>();
        for (T item : descriptor.rows()) {
            Row dataRow = sheet.createRow(rowIndex++);
            double maxHeight = fields.stream()
                    .mapToDouble(field -> field.annotation().height())
                    .max()
                    .orElse(14D);
            dataRow.setHeightInPoints((float) maxHeight);
            for (int column = 0; column < fields.size(); column++) {
                ExcelField metadata = fields.get(column);
                Cell cell = dataRow.createCell(column);
                cell.setCellStyle(dataStyle(workbook, styles, metadata.annotation()));
                Object source = metadata.annotation().isExport()
                        ? converter.readProperty(item, metadata)
                        : null;
                Object value = converter.exportValue(source, metadata, cell, workbook);
                writeCellValue(workbook, sheet, cell, value, metadata.annotation());

                if (metadata.annotation().isStatistics() && value != null) {
                    try {
                        statistics.merge(column, new BigDecimal(value.toString()), BigDecimal::add);
                    } catch (NumberFormatException ignored) {
                        // 非数字内容不参与统计，避免单个异常单元格中断整列汇总。
                    }
                }
            }
        }

        int lastDataRow = rowIndex - 1;

        if (!statistics.isEmpty()) {
            Row totalRow = sheet.createRow(rowIndex++);
            Cell titleCell = totalRow.createCell(0);
            titleCell.setCellValue("合计");
            titleCell.setCellStyle(styles.get("total"));
            statistics.forEach((column, total) -> {
                Cell cell = totalRow.createCell(column);
                cell.setCellValue(total.doubleValue());
                cell.setCellStyle(styles.get("total"));
            });
        }

        if (template) {
            int validationEnd = headerRowIndex + options.templateRows();
            addValidations(workbook, sheet, fields, headerRowIndex + 1, validationEnd);
        }
        mergeRepeatedCells(
                sheet,
                fields,
                headerRowIndex + 1,
                Math.max(headerRowIndex + 1, lastDataRow)
        );
        sheet.createFreezePane(0, headerRowIndex + 1);
    }

    private void addValidations(
            Workbook workbook,
            Sheet sheet,
            List<ExcelField> fields,
            int firstRow,
            int lastRow
    ) {
        for (int column = 0; column < fields.size(); column++) {
            Excel annotation = fields.get(column).annotation();
            List<String> choices = new ArrayList<>();
            if (annotation.combo().length > 0) {
                choices.addAll(List.of(annotation.combo()));
            } else if (annotation.comboReadDict()) {
                if (annotation.dictType().isBlank()) {
                    throw new ExcelException("comboReadDict=true 时必须配置 dictType：" + fields.get(column).propertyPath());
                }
                choices.addAll(dictResolver.labels(annotation.dictType()));
            }

            if (!choices.isEmpty()) {
                addHiddenSheetValidation(workbook, sheet, column, choices, firstRow, lastRow);
            }

            if (!annotation.prompt().isBlank()) {
                DataValidationHelper helper = sheet.getDataValidationHelper();
                DataValidationConstraint constraint = helper.createCustomConstraint("TRUE");
                CellRangeAddressList range = new CellRangeAddressList(firstRow, lastRow, column, column);
                DataValidation validation = helper.createValidation(constraint, range);
                validation.createPromptBox("填写提示", annotation.prompt());
                validation.setShowPromptBox(true);
                sheet.addValidationData(validation);
            }
        }
    }

    private static void addHiddenSheetValidation(
            Workbook workbook,
            Sheet targetSheet,
            int column,
            List<String> choices,
            int firstRow,
            int lastRow
    ) {
        String hiddenName = uniqueHiddenSheetName(workbook, column);
        Sheet hidden = workbook.createSheet(hiddenName);
        for (int index = 0; index < choices.size(); index++) {
            hidden.createRow(index).createCell(0).setCellValue(choices.get(index));
        }

        String rangeName = "excel_dict_" + Math.abs(hiddenName.hashCode());
        Name name = workbook.createName();
        name.setNameName(rangeName);
        name.setRefersToFormula("'" + hiddenName.replace("'", "''") + "'!$A$1:$A$" + choices.size());

        DataValidationHelper helper = targetSheet.getDataValidationHelper();
        DataValidationConstraint constraint = helper.createFormulaListConstraint(rangeName);
        CellRangeAddressList addressList = new CellRangeAddressList(firstRow, lastRow, column, column);
        DataValidation validation = helper.createValidation(constraint, addressList);
        validation.setShowErrorBox(true);
        if (validation instanceof XSSFDataValidation) {
            validation.setSuppressDropDownArrow(true);
        }
        targetSheet.addValidationData(validation);
        workbook.setSheetHidden(workbook.getSheetIndex(hidden), true);
    }

    private static Map<Integer, ExcelField> matchColumns(
            Row header,
            List<ExcelField> fields,
            DataFormatter formatter
    ) {
        Map<String, ExcelField> byName = new LinkedHashMap<>();
        for (ExcelField field : fields) {
            byName.put(field.annotation().name(), field);
        }

        Map<Integer, ExcelField> result = new LinkedHashMap<>();
        for (int column = 0; column < header.getLastCellNum(); column++) {
            String name = formatter.formatCellValue(header.getCell(column)).trim();
            ExcelField metadata = byName.get(name);
            if (metadata != null) {
                result.put(column, metadata);
            }
        }
        if (result.isEmpty()) {
            throw new ExcelException("Excel 表头与导入类型不匹配");
        }
        return result;
    }

    private static Object readCellValue(Cell cell, DataFormatter formatter) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return null;
        }
        CellType actualType = cell.getCellType() == CellType.FORMULA
                ? cell.getCachedFormulaResultType()
                : cell.getCellType();
        if (actualType == CellType.NUMERIC) {
            if (DateUtil.isCellDateFormatted(cell)) {
                return cell.getDateCellValue();
            }
            return BigDecimal.valueOf(cell.getNumericCellValue()).stripTrailingZeros().toPlainString();
        }
        if (actualType == CellType.BOOLEAN) {
            return cell.getBooleanCellValue();
        }
        if (actualType == CellType.ERROR) {
            throw new ExcelException("Excel 单元格包含错误值，位置：" + cell.getAddress());
        }
        if (actualType == CellType.STRING) {
            return cell.getStringCellValue().trim();
        }
        // 公式单元格只读取文件中已有的缓存结果，不在服务端重新执行公式。
        return formatter.formatCellValue(cell).trim();
    }

    private static boolean isEmptyRow(Row row, DataFormatter formatter) {
        if (row == null) {
            return true;
        }
        for (int column = row.getFirstCellNum(); column >= 0 && column < row.getLastCellNum(); column++) {
            Cell cell = row.getCell(column);
            if (cell != null && !formatter.formatCellValue(cell).isBlank()) {
                return false;
            }
        }
        return true;
    }

    private static void writeCellValue(
            Workbook workbook,
            Sheet sheet,
            Cell cell,
            Object value,
            Excel annotation
    ) {
        if (value == null) {
            cell.setBlank();
            return;
        }

        if (annotation.cellType() == Excel.ColumnType.IMAGE) {
            if (!(value instanceof byte[] bytes)) {
                throw new ExcelException("图片列仅支持 byte[]：" + annotation.name());
            }
            int pictureIndex = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
            CreationHelper helper = workbook.getCreationHelper();
            ClientAnchor anchor = helper.createClientAnchor();
            anchor.setCol1(cell.getColumnIndex());
            anchor.setCol2(cell.getColumnIndex() + 1);
            anchor.setRow1(cell.getRowIndex());
            anchor.setRow2(cell.getRowIndex() + 1);
            sheet.createDrawingPatriarch().createPicture(anchor, pictureIndex);
            return;
        }

        if (annotation.cellType() == Excel.ColumnType.NUMERIC && value instanceof Number number) {
            cell.setCellValue(number.doubleValue());
            return;
        }
        if (value instanceof Number number) {
            cell.setCellValue(number.doubleValue());
            return;
        }
        if (value instanceof Boolean bool) {
            cell.setCellValue(bool);
            return;
        }
        if (value instanceof Date date) {
            cell.setCellValue(date);
            return;
        }
        if (value instanceof LocalDate || value instanceof LocalDateTime) {
            cell.setCellValue(value.toString());
            return;
        }

        cell.setCellValue(sanitizeFormula(value.toString()));
    }

    private static String sanitizeFormula(String value) {
        if (value.isEmpty()) {
            return value;
        }
        char first = value.charAt(0);
        return first == '=' || first == '+' || first == '-' || first == '@'
                ? "'" + value
                : value;
    }

    private static Map<String, CellStyle> createStyles(Workbook workbook) {
        Map<String, CellStyle> styles = new HashMap<>();

        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 16);
        CellStyle title = workbook.createCellStyle();
        title.setAlignment(HorizontalAlignment.CENTER);
        title.setVerticalAlignment(VerticalAlignment.CENTER);
        title.setFont(titleFont);
        styles.put("title", title);

        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        CellStyle header = borderedStyle(workbook);
        header.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
        header.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        header.setAlignment(HorizontalAlignment.CENTER);
        header.setFont(headerFont);
        styles.put("header", header);

        CellStyle data = borderedStyle(workbook);
        data.setAlignment(HorizontalAlignment.CENTER);
        styles.put("data", data);

        CellStyle wrap = borderedStyle(workbook);
        wrap.setAlignment(HorizontalAlignment.LEFT);
        wrap.setWrapText(true);
        styles.put("wrap", wrap);

        Font totalFont = workbook.createFont();
        totalFont.setBold(true);
        CellStyle total = borderedStyle(workbook);
        total.setFont(totalFont);
        total.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        total.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styles.put("total", total);

        return styles;
    }

    private static CellStyle headerStyle(
            Workbook workbook,
            Map<String, CellStyle> cache,
            Excel annotation
    ) {
        String key = "header_" + annotation.headerBackgroundColor().getIndex()
                + "_" + annotation.headerColor().getIndex();
        return cache.computeIfAbsent(key, ignored -> {
            Font font = workbook.createFont();
            font.setBold(true);
            font.setColor(annotation.headerColor().getIndex());

            CellStyle style = borderedStyle(workbook);
            style.setFillForegroundColor(annotation.headerBackgroundColor().getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setFont(font);
            return style;
        });
    }

    private static CellStyle dataStyle(
            Workbook workbook,
            Map<String, CellStyle> cache,
            Excel annotation
    ) {
        boolean wrap = annotation.wrapText() || annotation.cellType() == Excel.ColumnType.TEXT;
        String key = "data_" + annotation.backgroundColor().getIndex()
                + "_" + annotation.color().getIndex()
                + "_" + annotation.align().name()
                + "_" + wrap;
        return cache.computeIfAbsent(key, ignored -> {
            Font font = workbook.createFont();
            font.setColor(annotation.color().getIndex());

            CellStyle style = borderedStyle(workbook);
            style.setFillForegroundColor(annotation.backgroundColor().getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style.setAlignment(annotation.align());
            style.setWrapText(wrap);
            style.setFont(font);
            return style;
        });
    }

    private static CellStyle borderedStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        return style;
    }

    private static int safeColumnWidth(double width) {
        double actual = Math.max(1, Math.min(width, 255));
        return (int) ((actual + 0.72) * 256);
    }

    private static void mergeRepeatedCells(
            Sheet sheet,
            List<ExcelField> fields,
            int firstDataRow,
            int lastDataRow
    ) {
        if (lastDataRow <= firstDataRow) {
            return;
        }
        DataFormatter formatter = new DataFormatter();
        for (int column = 0; column < fields.size(); column++) {
            if (!fields.get(column).annotation().needMerge()) {
                continue;
            }

            int start = firstDataRow;
            String previous = formattedCell(sheet, start, column, formatter);
            for (int row = firstDataRow + 1; row <= lastDataRow + 1; row++) {
                String current = row <= lastDataRow
                        ? formattedCell(sheet, row, column, formatter)
                        : null;
                if (!Objects.equals(previous, current)) {
                    if (row - start > 1 && previous != null && !previous.isBlank()) {
                        sheet.addMergedRegion(new CellRangeAddress(start, row - 1, column, column));
                    }
                    start = row;
                    previous = current;
                }
            }
        }
    }

    private static String formattedCell(Sheet sheet, int row, int column, DataFormatter formatter) {
        Row dataRow = sheet.getRow(row);
        return dataRow == null ? null : formatter.formatCellValue(dataRow.getCell(column));
    }

    private static String uniqueHiddenSheetName(Workbook workbook, int column) {
        int index = workbook.getNumberOfSheets();
        String name;
        do {
            name = "_excel_dict_" + column + "_" + index++;
        } while (workbook.getSheet(name) != null);
        return name;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void writeUnknownSheet(
            SXSSFWorkbook workbook,
            ExcelSheet<?> descriptor,
            ExcelOptions options,
            ExcelValueConverter converter
    ) {
        writeSheet(workbook, (ExcelSheet) descriptor, options, converter, false);
    }

    /**
     * 未接入业务字典实现时，仅在真正使用 dictType 时给出清晰错误。
     */
    private static final class MissingDictResolver implements ExcelDictResolver {

        @Override
        public String toLabel(String dictType, String value, String separator) {
            throw missing(dictType);
        }

        @Override
        public String toValue(String dictType, String label, String separator) {
            throw missing(dictType);
        }

        @Override
        public List<String> labels(String dictType) {
            throw missing(dictType);
        }

        private static ExcelException missing(String dictType) {
            return new ExcelException("未提供 ExcelDictResolver，无法解析字典：" + dictType);
        }
    }
}
