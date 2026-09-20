package dev.geo.admin.generator.controller;

import cn.hutool.core.convert.Convert;
import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import dev.geo.admin.common.annotation.Log;
import dev.geo.admin.common.core.controller.BaseController;
import dev.geo.admin.common.core.model.ApiResult;
import dev.geo.admin.common.core.model.PageResult;
import dev.geo.admin.common.enums.BusinessType;
import dev.geo.admin.common.utils.sql.SqlUtil;
import dev.geo.admin.generator.config.GenConfig;
import dev.geo.admin.generator.model.GenTable;
import dev.geo.admin.generator.model.GenTableColumn;
import dev.geo.admin.generator.model.dto.GenTablePageReqDTO;
import dev.geo.admin.generator.model.dto.GenImportReqDTO;
import dev.geo.admin.generator.model.dto.GenCreateTableReqDTO;
import dev.geo.admin.generator.service.IGenTableColumnService;
import dev.geo.admin.generator.service.IGenTableService;
import dev.geo.admin.security.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 代码生成 操作处理
 */
@Tag(name = "代码生成")
@RestController
@RequestMapping("/tool/gen")
@RequiredArgsConstructor
public class GenController extends BaseController {
    private final IGenTableService genTableService;

    private final IGenTableColumnService genTableColumnService;

    private final GenConfig genConfig;

    /**
     * 查询代码生成列表
     */
    @PreAuthorize("@se.hasPermission('tool:gen:list')")
    @GetMapping("/list")
    @Operation(summary = "分页查询已导入的表")
    public ApiResult<PageResult<GenTable>> genList(@Validated @ParameterObject GenTablePageReqDTO query) {
        PageResult<GenTable> list = genTableService.selectGenTablePage(query);
        return success(list);
    }

    /**
     * 获取代码生成信息
     */
    @PreAuthorize("@se.hasPermission('tool:gen:query')")
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取表生成配置", description = "info 为表配置，rows 为字段配置，tables 为已导入的表。")
    public ApiResult<Map<String, Object>> getInfo(@Parameter(description = "生成表配置 ID") @PathVariable Long id) {
        GenTable table = genTableService.selectGenTableById(id);
        List<GenTable> tables = genTableService.selectGenTableAll();
        List<GenTableColumn> list = genTableColumnService.selectGenTableColumnListByTableId(id);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("info", table);
        map.put("rows", list);
        map.put("tables", tables);
        return success(map);
    }

    /**
     * 查询数据库列表
     */
    @PreAuthorize("@se.hasPermission('tool:gen:list')")
    @GetMapping("/db/list")
    @Operation(summary = "分页查询可导入的数据库表")
    public ApiResult<PageResult<GenTable>> dataList(@Validated @ParameterObject GenTablePageReqDTO query) {
        PageResult<GenTable> list = genTableService.selectDbTablePage(query);
        return success(list);
    }

    /**
     * 查询数据表字段列表
     */
    @PreAuthorize("@se.hasPermission('tool:gen:list')")
    @GetMapping(value = "/column/{id}")
    @Operation(summary = "查询表字段配置")
    public ApiResult<List<GenTableColumn>> columnList(@Parameter(description = "生成表配置 ID") @PathVariable Long id) {
        List<GenTableColumn> list = genTableColumnService.selectGenTableColumnListByTableId(id);
        return success(list);
    }

    /**
     * 导入表结构（保存）
     */
    @PreAuthorize("@se.hasPermission('tool:gen:import')")
    @Log(title = "代码生成", businessType = BusinessType.IMPORT)
    @PostMapping("/importTable")
    @Operation(summary = "导入表结构")
    public ApiResult<Void> importTableSave(
            @Validated @RequestBody GenImportReqDTO request) {
        String[] tableNames = request.tables().toArray(String[]::new);
        // 查询表信息
        List<GenTable> tableList = genTableService.selectDbTableListByNames(tableNames);
        genTableService.importGenTable(tableList, SecurityUtils.getUsername());
        return success();
    }

    /**
     * 创建表结构（保存）
     */
    @PreAuthorize("@se.hasRole('admin')")
    @Log(title = "创建表", businessType = BusinessType.OTHER)
    @PostMapping("/createTable")
    @Operation(summary = "执行建表 SQL 并导入表结构", description = "仅允许管理员执行 CREATE TABLE 语句。")
    public ApiResult<Void> createTableSave(@Validated @RequestBody GenCreateTableReqDTO request) {
        try {
            String sql = request.sql();
            SqlUtil.filterKeyword(sql);
            List<SQLStatement> sqlStatements = SQLUtils.parseStatements(sql, DbType.mysql);
            if (sqlStatements.isEmpty() || sqlStatements.stream().anyMatch(statement -> !(statement instanceof MySqlCreateTableStatement))) {
                return error("仅支持 CREATE TABLE 建表语句");
            }
            List<String> tableNames = new ArrayList<>();
            for (SQLStatement sqlStatement : sqlStatements) {
                if (sqlStatement instanceof MySqlCreateTableStatement createTableStatement) {
                    if (genTableService.createTable(createTableStatement.toString())) {
                        String tableName = createTableStatement.getTableName().replaceAll("`", "");
                        tableNames.add(tableName);
                    }
                }
            }
            List<GenTable> tableList = genTableService.selectDbTableListByNames(tableNames.toArray(new String[0]));
            String operName = SecurityUtils.getUsername();
            genTableService.importGenTable(tableList, operName);
            return success();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return error("创建表结构异常");
        }
    }

    /**
     * 修改保存代码生成业务
     */
    @PreAuthorize("@se.hasPermission('tool:gen:edit')")
    @Log(title = "代码生成", businessType = BusinessType.UPDATE)
    @PutMapping
    @Operation(summary = "保存代码生成配置")
    public ApiResult<Void> editSave(@Validated @RequestBody GenTable genTable) {
        genTableService.validateEdit(genTable);
        genTableService.updateGenTable(genTable);
        return success();
    }

    /**
     * 删除代码生成
     */
    @PreAuthorize("@se.hasPermission('tool:gen:remove')")
    @Log(title = "代码生成", businessType = BusinessType.DELETE)
    @DeleteMapping("/{tableIds}")
    @Operation(summary = "删除代码生成配置", description = "只删除生成器中的配置，不删除数据库业务表。")
    public ApiResult<Void> remove(@Parameter(description = "生成表配置 ID 列表，多个用逗号分隔") @PathVariable Long[] tableIds) {
        genTableService.deleteGenTableByIds(tableIds);
        return success();
    }

    /**
     * 预览代码
     */
    @PreAuthorize("@se.hasPermission('tool:gen:preview')")
    @GetMapping("/preview/{id}")
    @Operation(summary = "预览生成代码", description = "返回文件路径与代码内容的映射，不写入文件。")
    public ApiResult<Map<String, String>> preview(@Parameter(description = "生成表配置 ID") @PathVariable("id") Long id) throws IOException {
        Map<String, String> dataMap = genTableService.previewCode(id);
        return success(dataMap);
    }

    /**
     * 生成代码（下载方式）
     */
    @PreAuthorize("@se.hasPermission('tool:gen:code')")
    @Log(title = "代码生成", businessType = BusinessType.GENCODE)
    @GetMapping("/download/{tableName}")
    @Operation(summary = "下载代码压缩包")
    public void download(HttpServletResponse response, @Parameter(description = "数据库表名") @PathVariable("tableName") String tableName) throws IOException {
        byte[] data = genTableService.downloadCode(tableName);
        genCode(response, data);
    }

    /**
     * 生成代码（自定义路径）
     */
    @PreAuthorize("@se.hasPermission('tool:gen:code')")
    @Log(title = "代码生成", businessType = BusinessType.GENCODE)
    @GetMapping("/genCode/{tableName}")
    @Operation(summary = "生成代码到指定目录", description = "写入服务器上的生成目录，需要开启 gen.allowOverwrite。")
    public ApiResult<Void> genCode(@Parameter(description = "数据库表名") @PathVariable("tableName") String tableName) {
        if (!genConfig.isAllowOverwrite()) {
            return error("【系统预设】不允许生成文件覆盖到本地");
        }
        genTableService.generatorCode(tableName);
        return success();
    }

    /**
     * 同步数据库
     */
    @PreAuthorize("@se.hasPermission('tool:gen:edit')")
    @Log(title = "代码生成", businessType = BusinessType.UPDATE)
    @GetMapping("/synchDb/{tableName}")
    @Operation(summary = "同步数据库表结构", description = "按数据库现有字段更新生成配置，不修改数据库表结构。")
    public ApiResult<Void> synchDb(@Parameter(description = "数据库表名") @PathVariable("tableName") String tableName) {
        genTableService.synchDb(tableName);
        return success();
    }

    /**
     * 批量生成代码
     */
    @PreAuthorize("@se.hasPermission('tool:gen:code')")
    @Log(title = "代码生成", businessType = BusinessType.GENCODE)
    @GetMapping("/batchGenCode")
    @Operation(summary = "批量下载生成代码")
    public void batchGenCode(HttpServletResponse response, @Parameter(description = "数据库表名，多个用逗号分隔") String tables) throws IOException {
        String[] tableNames = Convert.toStrArray(tables);
        byte[] data = genTableService.downloadCode(tableNames);
        genCode(response, data);
    }

    /**
     * 生成zip文件
     */
    private void genCode(HttpServletResponse response, byte[] data) throws IOException {
        response.reset();
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-Disposition", "attachment; filename=\"generated-code.zip\"");
        response.addHeader("Content-Length", "" + data.length);
        response.setContentType("application/octet-stream; charset=UTF-8");
        IOUtils.write(data, response.getOutputStream());
    }
}