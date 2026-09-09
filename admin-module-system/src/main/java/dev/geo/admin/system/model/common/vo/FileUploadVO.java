package dev.geo.admin.system.model.common.vo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "文件上传响应")
public record FileUploadVO(
        @Schema(description = "文件访问地址", example = "/profile/upload/2026/08/04/example.jpg")
        String url,

        @Schema(description = "文件保存路径或名称", example = "/upload/2026/08/04/example_20260804120000.jpg")
        String fileName,

        @Schema(description = "服务器保存的新文件名", example = "example_20260804120000.jpg")
        String newFileName,

        @Schema(description = "上传时的原始文件名", example = "example.jpg")
        String originalFilename
) {

}
