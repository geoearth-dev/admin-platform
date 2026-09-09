package dev.geo.admin.system.controller.common;

import cn.hutool.core.util.StrUtil;
import dev.geo.admin.common.config.AppConfig;
import dev.geo.admin.common.constant.Constants;
import dev.geo.admin.common.core.model.ApiResult;
import dev.geo.admin.common.utils.ServletUtils;
import dev.geo.admin.common.utils.file.FileUploadUtils;
import dev.geo.admin.common.utils.file.FileUtils;
import dev.geo.admin.excel.utils.ExcelToCsvUtil;
import dev.geo.admin.system.model.common.vo.ColumnRespVO;
import dev.geo.admin.system.model.common.dto.CsvColumnReqDTO;
import dev.geo.admin.system.model.common.dto.ExcelColumnReqDTO;
import dev.geo.admin.system.model.common.vo.FileUploadVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 通用请求处理
 */
@RestController
@RequestMapping("/common")
@RequiredArgsConstructor
public class CommonController {
    private static final Logger log = LoggerFactory.getLogger(CommonController.class);

    @Value("${ds.resource_url}")
    private String resourceUrl;


    @Value("${ds.hdfs.url}")
    private String hdfsUrl;

    private static final String FILE_DELIMETER = ",";

    /**
     * 通用下载请求
     *
     * @param fileName 文件名称
     * @param delete   是否删除
     */
    @GetMapping("/download")
    public void fileDownload(String fileName, Boolean delete, HttpServletResponse response, HttpServletRequest request) {
        try {
            if (!FileUtils.isDownloadAllowed(fileName)) {
                throw new Exception(StrUtil.format("文件名称({})非法，不允许下载。 ", fileName));
            }
            String realFileName = System.currentTimeMillis() + fileName.substring(fileName.indexOf("_") + 1);
            String filePath = AppConfig.getDownloadPath() + fileName;

            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            FileUtils.setAttachmentResponseHeader(response, realFileName);
            FileUtils.writeBytes(filePath, response.getOutputStream());
            if (delete) {
                FileUtils.deleteFile(filePath);
            }
        } catch (Exception e) {
            log.error("下载文件失败", e);
        }
    }

    /**
     * 通用上传请求（单个）
     */
    @PostMapping("/upload")
    public ApiResult<FileUploadVO> uploadFile(MultipartFile file) throws Exception {
        try {
            // 上传文件路径
            String filePath = AppConfig.getUploadPath();
            // 上传并返回新文件名称
            String fileName = FileUploadUtils.upload(filePath, file);
            String url = ServletUtils.getUrl() + fileName;
            FileUploadVO result = new FileUploadVO(url, fileName, FileUtils.getName(fileName), file.getOriginalFilename());
            return ApiResult.success(result);
        } catch (Exception e) {
            return ApiResult.error(e.getMessage());
        }
    }

    /**
     * 通用上传请求（多个）
     */
    @PostMapping("/uploads")
    public ApiResult<List<FileUploadVO>> uploadFiles(List<MultipartFile> files) throws Exception {
        try {
            List<FileUploadVO> results = new ArrayList<>();
            // 上传文件路径
            String filePath = AppConfig.getUploadPath();
            for (MultipartFile file : files) {
                // 上传并返回新文件名称
                String fileName = FileUploadUtils.upload(filePath, file);
                String url = ServletUtils.getUrl() + fileName;
                results.add(new FileUploadVO(url, fileName, FileUtils.getName(fileName), file.getOriginalFilename()));
            }
            return ApiResult.success(results);
        } catch (Exception e) {
            return ApiResult.error(e.getMessage());
        }
    }

    /**
     * 本地资源通用下载
     */
    @GetMapping("/download/resource")
    public void resourceDownload(String resource, HttpServletRequest request, HttpServletResponse response) {
        try {
            if (!FileUtils.isDownloadAllowed(resource)) {
                throw new Exception(StrUtil.format("资源文件({})非法，不允许下载。 ", resource));
            }
            // 本地资源路径
            String localPath = AppConfig.getProfile();
            // 数据库资源地址
            String downloadPath = localPath + FileUtils.stripPrefix(resource);
            // 下载名称
            String downloadName = StrUtil.subAfter(downloadPath, "/", true);
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            FileUtils.setAttachmentResponseHeader(response, downloadName);
            FileUtils.writeBytes(downloadPath, response.getOutputStream());
        } catch (Exception e) {
            log.error("下载文件失败", e);
        }
    }


    /**
     * 获取excel列名并转换为csv
     *
     * @return ApiResult
     */
    @PostMapping("/getExcelColumn")
    public ApiResult<ColumnRespVO> getExcelColumn(@RequestBody ExcelColumnReqDTO excelColumnReq) {
        String excelFile = excelColumnReq.getExcelFile();
        excelFile = AppConfig.getProfile() + excelFile.replace(Constants.RESOURCE_PREFIX + "/", "");
        excelFile = excelFile.replace("/", File.separator);
        Integer startColumn = excelColumnReq.getStartColumn();
        Integer startData = excelColumnReq.getStartData();
        String fileName = UUID.randomUUID().toString().replace("-", "") + ".csv";
        String csvFile = resourceUrl + "csv" + File.separator + fileName;
        List<String> columnList = ExcelToCsvUtil.convertExcelToCsv(excelFile, csvFile, startColumn, startData);
        if (!columnList.isEmpty()) {
            if (!ExcelToCsvUtil.verifyColumn(columnList)) {
                return ApiResult.error("附件中列名格式有误，请检查!");
            }
        }
        String hdfsPath = "/tmp/etl";
        uploadHdfs(hdfsUrl, hdfsPath, csvFile, fileName);
        return ApiResult.success(ColumnRespVO.builder()
                .csvFile(hdfsUrl + "/" + hdfsPath + "/" + fileName)
                .columnList(columnList).build());
    }

    /**
     * 获取csv列名
     *
     * @return ApiResult
     */
    @PostMapping("/getCsvColumn")
    public ApiResult<ColumnRespVO> getCsvColumn(@RequestBody CsvColumnReqDTO csvColumnReqVO) {
        String file = csvColumnReqVO.getFile();
        file = AppConfig.getProfile() + file.replace(Constants.RESOURCE_PREFIX + "/", "");
        file = file.replace("/", File.separator);
        String fileName = UUID.randomUUID().toString().replace("-", "") + ".csv";
        String csvFile = resourceUrl + "csv" + File.separator + fileName;
        List<String> columnList = ExcelToCsvUtil.parseCsv(file, csvFile);
        if (!columnList.isEmpty()) {
            if (!ExcelToCsvUtil.verifyColumn(columnList)) {
                return ApiResult.error("附件中列名格式有误，请检查!");
            }
        }
        String hdfsPath = "/tmp/etl";
        uploadHdfs(hdfsUrl, hdfsPath, csvFile, fileName);
        return ApiResult.success(ColumnRespVO.builder()
                .csvFile(hdfsUrl + "/" + hdfsPath + "/" + fileName)
                .columnList(columnList).build());
    }

    /**
     * 获取txt列名
     *
     * @return ApiResult
     */
    @PostMapping("/getTxtColumn")
    public ApiResult<ColumnRespVO> getTxtColumn(@RequestBody CsvColumnReqDTO csvColumnReqVO) {
        String file = csvColumnReqVO.getFile();
        file = AppConfig.getProfile() + file.replace(Constants.RESOURCE_PREFIX + "/", "");
        file = file.replace("/", File.separator);
        String fileName = UUID.randomUUID().toString().replace("-", "") + ".txt";
        String csvFile = resourceUrl + "txt" + File.separator + fileName;
        List<String> columnList = ExcelToCsvUtil.parseTxt(file, csvFile);
        if (columnList.size() > 0) {
            if (!ExcelToCsvUtil.verifyColumn(columnList)) {
                return ApiResult.error("附件中列名格式有误，请检查!");
            }
        }
        String hdfsPath = "/tmp/etl";
        uploadHdfs(hdfsUrl, hdfsPath, csvFile, fileName);
        return ApiResult.success(ColumnRespVO.builder()
                .csvFile(hdfsUrl + "/" + hdfsPath + "/" + fileName)
                .columnList(columnList).build());
    }

    /**
     * 上传文件至hdfs
     *
     * @param hdfsUrl  hdfs地址
     * @param pathStr  上传的路径
     * @param file     文件路径
     * @param filename 文件名称
     */
    public void uploadHdfs(String hdfsUrl, String pathStr, String file, String filename) {
        pathStr = pathStr == null ? "" : pathStr;
        pathStr = resolvePath(pathStr, filename);
//        // 1. 创建 Hadoop 配置对象
//        Configuration conf = new Configuration();
//        conf.set("fs.defaultFS", hdfsUrl);
//        conf.set("dfs.client.use.datanode.hostname", "true");
//        // 如果只有1台DN，确保副本数别比节点数大
//        conf.set("dfs.replication", "1");
//        Path path = new Path(pathStr);
//        try (FileSystem fs = FileSystem.get(new URI(hdfsUrl), conf, "hadoop");
//             InputStream inputStream = new FileInputStream(file);
//             FSDataOutputStream outputStream = fs.create(path)) {
//            IOUtils.copy(inputStream, outputStream);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private String resolvePath(String path, String filename) {
        String str = path + "/" + filename;
        return str.replaceAll("/+", "/");
    }
}
