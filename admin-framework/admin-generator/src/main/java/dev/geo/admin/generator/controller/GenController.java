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
import dev.geo.admin.generator.service.IGenTableColumnService;
import dev.geo.admin.generator.service.IGenTableService;
import dev.geo.admin.security.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
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
    public ApiResult<PageResult<GenTable>> genList(GenTablePageReqDTO query) {
        PageResult<GenTable> list = genTableService.selectGenTablePage(query);
        return success(list);
    }

    /**
     * 获取代码生成信息
     */
    @PreAuthorize("@se.hasPermission('tool:gen:query')")
    @GetMapping(value = "/{tableId}")
    public ApiResult<Map<String, Object>> getInfo(@PathVariable Long tableId) {
        GenTable table = genTableService.selectGenTableById(tableId);
        List<GenTable> tables = genTableService.selectGenTableAll();
        List<GenTableColumn> list = genTableColumnService.selectGenTableColumnListByTableId(tableId);
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
    public ApiResult<PageResult<GenTable>> dataList(GenTablePageReqDTO query) {
        PageResult<GenTable> list = genTableService.selectDbTablePage(query);
        return success(list);
    }

    /**
     * 查询数据表字段列表
     */
    @PreAuthorize("@se.hasPermission('tool:gen:list')")
    @GetMapping(value = "/column/{tableId}")
    public ApiResult<List<GenTableColumn>> columnList(@PathVariable Long tableId) {
        List<GenTableColumn> list = genTableColumnService.selectGenTableColumnListByTableId(tableId);
        return success(list);
    }

    /**
     * 导入表结构（保存）
     */
    @PreAuthorize("@se.hasPermission('tool:gen:import')")
    @Log(title = "代码生成", businessType = BusinessType.IMPORT)
    @PostMapping("/importTable")
    public ApiResult<Void> importTableSave(@RequestParam("tables") String tables, @RequestParam("tplWebType") String tplWebType) {
        String[] tableNames = Convert.toStrArray(tables);
        // 查询表信息
        List<GenTable> tableList = genTableService.selectDbTableListByNames(tableNames);
        genTableService.importGenTable(tableList, tplWebType, SecurityUtils.getUsername());
        return success();
    }

    /**
     * 创建表结构（保存）
     */
    @PreAuthorize("@se.hasRole('admin')")
    @Log(title = "创建表", businessType = BusinessType.OTHER)
    @PostMapping("/createTable")
    public ApiResult<Void> createTableSave(@RequestParam("sql") String sql, @RequestParam("tplWebType") String tplWebType) {
        try {
            SqlUtil.filterKeyword(sql);
            List<SQLStatement> sqlStatements = SQLUtils.parseStatements(sql, DbType.mysql);
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
            genTableService.importGenTable(tableList, tplWebType, operName);
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
    public ApiResult<Void> remove(@PathVariable Long[] tableIds) {
        genTableService.deleteGenTableByIds(tableIds);
        return success();
    }

    /**
     * 预览代码
     */
    @PreAuthorize("@se.hasPermission('tool:gen:preview')")
    @GetMapping("/preview/{tableId}")
    public ApiResult<Map<String, String>> preview(@PathVariable("tableId") Long tableId) throws IOException {
        Map<String, String> dataMap = genTableService.previewCode(tableId);
        return success(dataMap);
    }

    /**
     * 生成代码（下载方式）
     */
    @PreAuthorize("@se.hasPermission('tool:gen:code')")
    @Log(title = "代码生成", businessType = BusinessType.GENCODE)
    @GetMapping("/download/{tableName}")
    public void download(HttpServletResponse response, @PathVariable("tableName") String tableName) throws IOException {
        byte[] data = genTableService.downloadCode(tableName);
        genCode(response, data);
    }

    /**
     * 生成代码（自定义路径）
     */
    @PreAuthorize("@se.hasPermission('tool:gen:code')")
    @Log(title = "代码生成", businessType = BusinessType.GENCODE)
    @GetMapping("/genCode/{tableName}")
    public ApiResult<Void> genCode(@PathVariable("tableName") String tableName) {
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
    public ApiResult<Void> synchDb(@PathVariable("tableName") String tableName) {
        genTableService.synchDb(tableName);
        return success();
    }

    /**
     * 批量生成代码
     */
    @PreAuthorize("@se.hasPermission('tool:gen:code')")
    @Log(title = "代码生成", businessType = BusinessType.GENCODE)
    @GetMapping("/batchGenCode")
    public void batchGenCode(HttpServletResponse response, String tables) throws IOException {
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
        response.setHeader("Content-Disposition", "attachment; filename=\"ruoyi.zip\"");
        response.addHeader("Content-Length", "" + data.length);
        response.setContentType("application/octet-stream; charset=UTF-8");
        IOUtils.write(data, response.getOutputStream());
    }
}