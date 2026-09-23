package dev.geo.admin.system.controller.common;

import cn.hutool.core.util.StrUtil;
import dev.geo.admin.common.config.AppConfig;
import dev.geo.admin.common.core.model.ApiResult;
import dev.geo.admin.common.utils.ServletUtils;
import dev.geo.admin.common.utils.file.FileUploadUtils;
import dev.geo.admin.common.utils.file.FileUtils;
import dev.geo.admin.common.utils.file.MimeTypeUtils;
import dev.geo.admin.system.model.common.vo.FileUploadVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * 通用请求处理
 */
@Tag(name = "文件管理")
@RestController
@RequestMapping("/common")
@RequiredArgsConstructor
public class CommonController {
    private static final Logger log = LoggerFactory.getLogger(CommonController.class);


    /**
     * 通用下载请求
     *
     * @param fileName 文件名称
     * @param delete   是否删除
     */
    @GetMapping("/download")
    @Operation(summary = "下载临时文件", description = "从临时下载目录读取文件；delete=true 时下载后删除源文件。")
    public void fileDownload(@Parameter(description = "临时下载目录中的文件名") String fileName,
                             @Parameter(description = "下载后是否删除源文件") Boolean delete, HttpServletResponse response, HttpServletRequest request) {
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
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    @Operation(summary = "上传单个文件", description = "使用 multipart/form-data 提交 file，返回访问地址及保存路径。")
    public ApiResult<FileUploadVO> uploadFile(@Parameter(description = "待上传的文件") @RequestParam("file") MultipartFile file) throws Exception {
        try {
            // 上传文件路径
            String filePath = AppConfig.getUploadPath();
            // 上传并返回新文件名称
            String fileName = FileUploadUtils.upload(filePath, file, dev.geo.admin.common.utils.file.MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION, true);
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
    @PostMapping(value = "/uploads", consumes = "multipart/form-data")
    @Operation(summary = "批量上传文件", description = "使用 multipart/form-data 提交 files，按提交顺序返回上传结果。")
    public ApiResult<List<FileUploadVO>> uploadFiles(@Parameter(description = "待上传的文件列表") @RequestParam("files") List<MultipartFile> files) throws Exception {
        try {
            List<FileUploadVO> results = new ArrayList<>();
            // 上传文件路径
            String filePath = AppConfig.getUploadPath();
            for (MultipartFile file : files) {
                // 上传并返回新文件名称
                String fileName = FileUploadUtils.upload(filePath, file, MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION, true);
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
    @Operation(summary = "下载已上传的文件")
    public void resourceDownload(@Parameter(description = "上传接口返回的 fileName 路径") String resource, HttpServletRequest request, HttpServletResponse response) {
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

}
