package dev.geo.admin.generator.util;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import dev.geo.admin.common.constant.DateTimeFormat;
import dev.geo.admin.common.exception.ServiceException;
import dev.geo.admin.generator.model.GenTable;
import dev.geo.admin.generator.model.GenTableColumn;
import org.apache.velocity.VelocityContext;
import javax.lang.model.SourceVersion;
import java.time.Instant;
import java.util.*;

/** Project-specific generator: Element Plus + TypeScript setup, BaseMapperX CRUD. */
public final class VelocityUtils {
    private VelocityUtils() {}

    public static VelocityContext prepareContext(GenTable table) {
        validate(table);
        JSONObject options = options(table);
        table.setTreeCode(options.getString("treeCode"));
        table.setTreeParentCode(options.getString("treeParentCode"));
        table.setTreeName(options.getString("treeName"));
        VelocityContext context = new VelocityContext();
        context.put("table", table);
        context.put("columns", table.getColumns());
        context.put("pkColumn", table.getPkColumn());
        context.put("ClassName", table.getClassName());
        context.put("className", StrUtil.lowerFirst(table.getClassName()));
        context.put("BusinessName", StrUtil.upperFirst(table.getBusinessName()));
        context.put("businessName", table.getBusinessName());
        context.put("moduleName", table.getModuleName());
        context.put("packageName", table.getPackageName());
        context.put("permissionPrefix", table.getModuleName() + ":" + table.getBusinessName());
        context.put("functionName", comment(table.getFunctionName()));
        context.put("functionLabel", JSON.toJSONString(table.getFunctionName()).replace("<", "\\u003c"));
        context.put("functionSql", Objects.requireNonNullElse(table.getFunctionName(), table.getTableName()).replace("'", "''"));
        context.put("author", comment(table.getFunctionAuthor()));
        context.put("datetime", DateTimeFormat.DATE_TIME.withZone(DateTimeFormat.BUSINESS_ZONE).format(Instant.now()));
        context.put("parentMenuId", options.getLongValue("parentMenuId"));
        context.put("genView", options.getBooleanValue("genView"));
        context.put("colSpan", 24 / (table.getFormColNum() == null || table.getFormColNum() < 1 || table.getFormColNum() > 3 ? 1 : table.getFormColNum()));
        if (table.isTree()) {
            GenTableColumn code = column(table, table.getTreeCode());
            GenTableColumn parent = column(table, table.getTreeParentCode());
            GenTableColumn label = column(table, table.getTreeName());
            if (!code.isPk() || !code.getJavaType().equals(parent.getJavaType())) {
                throw new ServiceException("树编码必须为主键，父编码类型须与主键一致");
            }
            context.put("treeCode", code.getJavaField());
            context.put("treeParentCode", parent.getJavaField());
            context.put("treeParentColumn", parent);
            context.put("treeName", label.getJavaField());
        }
        if (table.isSub()) {
            GenTable sub = table.getSubTable();
            if (sub == null || sub.getTableName().equals(table.getTableName())) throw new ServiceException("请选择已导入的其他表作为子表");
            validate(sub);
            if (sub.getClassName().equals(table.getClassName())) throw new ServiceException("主表和子表实体类名不能相同");
            GenTableColumn fk = column(sub, table.getSubTableFkName());
            if (!fk.getJavaType().equals(table.getPkColumn().getJavaType())) throw new ServiceException("子表外键类型须与主表主键一致");
            context.put("subTable", sub);
            context.put("subClassName", sub.getClassName());
            context.put("subclassName", StrUtil.lowerFirst(sub.getClassName()));
            context.put("subFk", fk);
        }
        List<GenTableColumn> uiColumns = new ArrayList<>(table.getColumns());
        if (table.isSub()) uiColumns.addAll(table.getSubTable().getColumns());
        Set<String> dictTypes = new LinkedHashSet<>();
        for (GenTableColumn c : uiColumns) {
            if (StrUtil.isNotBlank(c.getDictType())) {
                if (!c.getDictType().matches("[A-Za-z0-9_]+")) throw new ServiceException("非法字典类型");
                dictTypes.add(c.getDictType());
            }
        }
        context.put("dictTypes", new ArrayList<>(dictTypes));
        context.put("hasUpload", uiColumns.stream().anyMatch(c -> "image".equals(c.getHtmlType()) || "upload".equals(c.getHtmlType())));
        context.put("hasEditor", uiColumns.stream().anyMatch(c -> "editor".equals(c.getHtmlType())));
        return context;
    }

    private static String comment(String value) { return value == null ? "" : value.replace("*/", "* /").replace('\n', ' ').replace('\r', ' '); }
    private static JSONObject options(GenTable table) { JSONObject value = JSON.parseObject(table.getOptions()); return value == null ? new JSONObject() : value; }
    private static GenTableColumn column(GenTable table, String name) {
        return table.getColumns().stream().filter(c -> c.getColumnName().equals(name)).findFirst().orElseThrow(() -> new ServiceException("配置字段不存在：" + name));
    }
    private static void identifier(String value) {
        if (value == null || !SourceVersion.isIdentifier(value) || SourceVersion.isKeyword(value)) throw new ServiceException("非法 Java 名称：" + value);
    }
    private static void validate(GenTable table) {
        if (table == null || table.getPkColumn() == null) throw new ServiceException("请先配置单列主键");
        if (table.getPackageName() == null || !SourceVersion.isName(table.getPackageName())) throw new ServiceException("生成包名无效");
        if (!List.of("Long", "Integer", "String").contains(table.getPkColumn().getJavaType())) throw new ServiceException("主键类型仅支持 Long、Integer、String");
        if (table.getPkColumn().isIncrement() && "String".equals(table.getPkColumn().getJavaType())) throw new ServiceException("字符串主键不能配置自增");
        identifier(table.getClassName()); identifier(table.getBusinessName()); identifier(table.getModuleName());
        if (!List.of("crud", "tree", "sub").contains(table.getTplCategory())) throw new ServiceException("不支持的生成模板");
        Set<String> fields = new HashSet<>();
        for (GenTableColumn c : table.getColumns()) {
            identifier(c.getJavaField());
            if (!fields.add(c.getJavaField())) throw new ServiceException("Java 属性重复：" + c.getJavaField());
            if (!List.of("String", "Long", "Integer", "Double", "BigDecimal", "Boolean", "Instant", "LocalDate", "LocalDateTime", "LocalTime", "Date").contains(c.getJavaType())) throw new ServiceException("不支持的 Java 类型：" + c.getJavaType());
            if (!c.getColumnName().matches("[A-Za-z_][A-Za-z0-9_$]*")) throw new ServiceException("不支持的列名：" + c.getColumnName());
            if ("LIKE".equals(c.getQueryType()) && !"String".equals(c.getJavaType())) throw new ServiceException("LIKE 查询仅支持 String 字段：" + c.getJavaField());
        }
        if (!table.getTableName().matches("[A-Za-z_][A-Za-z0-9_$]*")) throw new ServiceException("不支持的表名");
    }

    public static List<String> getTemplateList(GenTable table) {
        List<String> result = new ArrayList<>(List.of("vm/java/entity.java.vm", "vm/java/mapper.java.vm",
                "vm/java/page-req-dto.java.vm", "vm/java/save-dto.java.vm", "vm/java/service.java.vm",
                "vm/java/service-impl.java.vm", "vm/java/controller.java.vm", "vm/sql/sql.vm",
                "vm/ts/api.ts.vm", "vm/ts/type.ts.vm", "vm/vue/v3ts/index.vue.vm"));
        if (table.isSub()) result.addAll(List.of("vm/java/sub-entity.java.vm", "vm/java/sub-mapper.java.vm", "vm/java/sub-save-dto.java.vm"));
        if (options(table).getBooleanValue("genView")) result.add("vm/vue/v3ts/view.vue.vm");
        return result;
    }

    public static String getFileName(String template, GenTable table) {
        String java = "admin-module-" + table.getModuleName() + "/src/main/java/" + table.getPackageName().replace('.', '/') + "/";
        String web = "admin-ui/src/";
        String name = table.getClassName();
        String route = table.getModuleName() + "/" + table.getBusinessName();
        return switch (template) {
            case "vm/java/entity.java.vm" -> java + "model/" + table.getModuleName() + "/entity/" + name + ".java";
            case "vm/java/sub-entity.java.vm" -> java + "model/" + table.getModuleName() + "/entity/" + table.getSubTable().getClassName() + ".java";
            case "vm/java/mapper.java.vm" -> java + "mapper/" + table.getModuleName() + "/" + name + "Mapper.java";
            case "vm/java/sub-mapper.java.vm" -> java + "mapper/" + table.getModuleName() + "/" + table.getSubTable().getClassName() + "Mapper.java";
            case "vm/java/page-req-dto.java.vm" -> java + "model/" + table.getModuleName() + "/dto/" + name + "PageReqDTO.java";
            case "vm/java/save-dto.java.vm" -> java + "model/" + table.getModuleName() + "/dto/" + name + "SaveDTO.java";
            case "vm/java/sub-save-dto.java.vm" -> java + "model/" + table.getModuleName() + "/dto/" + table.getSubTable().getClassName() + "SaveDTO.java";
            case "vm/java/service.java.vm" -> java + "service/" + table.getModuleName() + "/I" + name + "Service.java";
            case "vm/java/service-impl.java.vm" -> java + "service/" + table.getModuleName() + "/impl/" + name + "ServiceImpl.java";
            case "vm/java/controller.java.vm" -> java + "controller/" + table.getModuleName() + "/" + name + "Controller.java";
            case "vm/ts/api.ts.vm" -> web + "api/" + route + ".ts";
            case "vm/ts/type.ts.vm" -> web + "types/base/api/" + route + ".ts";
            case "vm/vue/v3ts/index.vue.vm" -> web + "views/sys/" + route + "/index.vue";
            case "vm/vue/v3ts/view.vue.vm" -> web + "views/sys/" + route + "/view.vue";
            case "vm/sql/sql.vm" -> "sql/" + table.getBusinessName() + "-menu.sql";
            default -> throw new ServiceException("未知模板：" + template);
        };
    }
}
