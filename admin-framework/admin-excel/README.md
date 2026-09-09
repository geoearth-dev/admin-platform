# admin-excel

`admin-excel` 是平台独立的 Excel 导入导出模块，基于 Apache POI，并通过扩展接口隔离系统字典、Redis 和具体业务实体。

## 已提供能力

- 基于 `@Excel`、`@Excels` 的注解式字段映射
- 单 Sheet 和多 Sheet 导出
- Excel 导入模板与下拉选项
- 导入时的标题匹配和基础类型转换
- 字典值与标签双向转换扩展点
- `readConverterExp` 固定表达式转换
- 自定义 `ExcelHandlerAdapter`
- 日期、数字、布尔值、图片字节导出
- 指定包含或排除字段
- 统计行与纵向合并
- `SXSSFWorkbook` 流式导出
- HTTP 下载响应头处理
- 基础公式注入防护

## 基本导出

```java
excelService.exportExcel(response, rows, UserExportVO.class, "用户数据");
```

HTTP 响应的 Content-Type 和字符编码由 `exportExcel` 统一设置；下载文件名由前端下载组件指定。

## 导出模板

```java
excelService.exportTemplate(
        outputStream,
        UserImportVO.class,
        "用户数据",
        "用户导入模板"
);
```

## 导入

```java
List<UserImportVO> rows = excelService.importExcel(
        multipartFile.getInputStream(),
        UserImportVO.class
);
```

## 多 Sheet 导出

```java
List<ExcelSheet<?>> sheets = List.of(
        new ExcelSheet<>("用户", users, UserExportVO.class),
        new ExcelSheet<>("角色", roles, RoleExportVO.class)
);

excelService.exportMultiSheet(response.getOutputStream(), sheets);
```

## 字典扩展

Excel 模块只定义 `ExcelDictResolver`，业务模块按需实现该接口：

```java
@Component
@RequiredArgsConstructor
public class SystemExcelDictResolver implements ExcelDictResolver {

    private final DictCacheService dictCacheService;

    @Override
    public String toLabel(String type, String value, String separator) {
        return dictCacheService.getDictLabel(type, value, separator);
    }

    @Override
    public String toValue(String type, String label, String separator) {
        return dictCacheService.getDictValue(type, label, separator);
    }

    @Override
    public List<String> labels(String type) {
        List<SysDictData> rows = dictCacheService.getDictCache(type);
        return rows == null
                ? List.of()
                : rows.stream().map(SysDictData::getDictLabel).toList();
    }
}
```

没有使用 `dictType` 时不需要提供实现；首次使用字典字段时若没有实现，模块会抛出明确异常。

## 使用约束

- 推荐使用专门的导入、导出 VO，不要直接导出数据库实体中的密码、密钥和内部字段。
- `ExcelService` 是无状态 Spring 单例；工作簿和 Sheet 状态仅存在于单次方法调用中。
- `ExcelHandlerAdapter` 实现应保持无状态。
- 图片列目前只接受 `byte[]`，不在 Excel 核心模块中主动访问网络或本地任意路径。
- 调用方负责提供并管理输入流、输出流；模块不会自行拼接服务器下载目录。
