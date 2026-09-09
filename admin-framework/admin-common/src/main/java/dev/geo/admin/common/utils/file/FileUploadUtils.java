package dev.geo.admin.common.utils.file;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import dev.geo.admin.common.config.AppConfig;
import dev.geo.admin.common.constant.Constants;
import dev.geo.admin.common.constant.DateTimeFormat;
import dev.geo.admin.common.exception.file.FileNameLengthLimitExceededException;
import dev.geo.admin.common.exception.file.FileSizeLimitExceededException;
import dev.geo.admin.common.exception.file.InvalidExtensionException;
import dev.geo.admin.common.utils.Seq;
import lombok.Getter;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Objects;

/**
 * 文件上传工具类
 */
public class FileUploadUtils {
    /**
     * 默认大小 50M
     */
    public static final long DEFAULT_MAX_SIZE = 50 * 1024 * 1024L;

    /**
     * 默认的文件名最大长度 100
     */
    public static final int DEFAULT_FILE_NAME_LENGTH = 100;

    /**
     * 默认上传的地址
     */
    @Getter
    private static String defaultBaseDir = AppConfig.getProfile();

    public static void setDefaultBaseDir(String defaultBaseDir) {
        FileUploadUtils.defaultBaseDir = defaultBaseDir;
    }

    /**
     * 以默认配置进行文件上传
     *
     * @param file 上传的文件
     * @return 文件名称
     */
    public static String upload(MultipartFile file) throws IOException {
        try {
            return upload(getDefaultBaseDir(), file, MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION);
        } catch (Exception e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    /**
     * 根据文件路径上传
     *
     * @param baseDir 相对应用的基目录
     * @param file    上传的文件
     * @return 文件名称
     */
    public static String upload(String baseDir, MultipartFile file) throws IOException {
        try {
            return upload(baseDir, file, MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION);
        } catch (Exception e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    /**
     * 文件上传
     *
     * @param baseDir          相对应用的基目录
     * @param file             上传的文件
     * @param allowedExtension 上传文件类型
     * @return 返回上传成功的文件名
     * @throws FileSizeLimitExceededException       如果超出最大大小
     * @throws FileNameLengthLimitExceededException 文件名太长
     * @throws IOException                          比如读写文件出错时
     * @throws InvalidExtensionException            文件校验异常
     */
    public static String upload(String baseDir, MultipartFile file, String[] allowedExtension) throws FileSizeLimitExceededException, IOException, FileNameLengthLimitExceededException, InvalidExtensionException {
        return upload(baseDir, file, allowedExtension, false);
    }

    /**
     * 文件上传
     *
     * @param baseDir          相对应用的基目录
     * @param file             上传的文件
     * @param useCustomNaming  系统自定义文件名
     * @param allowedExtension 上传文件类型
     * @return 返回上传成功的文件名
     * @throws FileSizeLimitExceededException       如果超出最大大小
     * @throws FileNameLengthLimitExceededException 文件名太长
     * @throws IOException                          比如读写文件出错时
     * @throws InvalidExtensionException            文件校验异常
     */
    public static String upload(String baseDir, MultipartFile file, String[] allowedExtension, boolean useCustomNaming) throws FileSizeLimitExceededException, IOException, FileNameLengthLimitExceededException, InvalidExtensionException {
        int fileNameLength = Objects.requireNonNull(file.getOriginalFilename()).length();
        if (fileNameLength > FileUploadUtils.DEFAULT_FILE_NAME_LENGTH) {
            throw new FileNameLengthLimitExceededException(FileUploadUtils.DEFAULT_FILE_NAME_LENGTH);
        }

        assertAllowed(file, allowedExtension);

        String fileName = useCustomNaming ? uuidFilename(file) : extractFilename(file);

        String absPath = getAbsoluteFile(baseDir, fileName).getAbsolutePath();
        file.transferTo(Paths.get(absPath));
        return getPathFileName(baseDir, fileName);
    }

    /**
     * 编码文件名(日期格式目录 + 原文件名 + 序列值 + 后缀)
     */
    public static String extractFilename(MultipartFile file) {
        return StrUtil.format("{}/{}_{}.{}", DateTimeFormat.DATE_PATH.format(Instant.now()), FilenameUtils.getBaseName(file.getOriginalFilename()), Seq.getId(Seq.uploadSeqType), getExtension(file));
    }

    /**
     * 编编码文件名(日期格式目录 + UUID + 后缀)
     */
    public static String uuidFilename(MultipartFile file) {
        return StrUtil.format("{}/{}.{}", DateTimeFormat.DATE_PATH.format(Instant.now()), IdUtil.fastSimpleUUID(), getExtension(file));
    }

    public static File getAbsoluteFile(String uploadDir, String fileName) throws IOException {
//        File desc = new File(uploadDir + File.separator + fileName);
//
//        if (!desc.exists()) {
//            if (!desc.getParentFile().exists()) {
//                desc.getParentFile().mkdirs();
//            }
//        }
//        return desc;

        Path target = Path.of(uploadDir).resolve(fileName).toAbsolutePath().normalize();
        Path parent = target.getParent();
        if (parent != null) {
        /* Files.createDirectories() 的优点是：
            目录已经存在时不会报错。
            自动创建多级目录。
            创建失败直接抛出 IOException。
            不会出现忽略 boolean 结果的警告。*/
            Files.createDirectories(parent);
        }
        return target.toFile();
    }

    public static String getPathFileName(String uploadDir, String fileName) {
        int dirLastIndex = AppConfig.getProfile().length() + 1;
        String currentDir = StrUtil.subSuf(uploadDir, dirLastIndex);
        return Constants.RESOURCE_PREFIX + "/" + currentDir + "/" + fileName;
    }

    /**
     * 文件大小校验
     *
     * @param file 上传的文件
     * @throws FileSizeLimitExceededException 如果超出最大大小
     * @throws InvalidExtensionException      InvalidExtensionException
     */
    public static void assertAllowed(MultipartFile file, String[] allowedExtension) throws FileSizeLimitExceededException, InvalidExtensionException {
        long size = file.getSize();
        if (size > DEFAULT_MAX_SIZE) {
            throw new FileSizeLimitExceededException(DEFAULT_MAX_SIZE / 1024 / 1024);
        }

        String fileName = file.getOriginalFilename();
        String extension = getExtension(file);
        if (allowedExtension != null && !isAllowedExtension(extension, allowedExtension)) {
            if (allowedExtension == MimeTypeUtils.IMAGE_EXTENSION) {
                throw new InvalidExtensionException.InvalidImageExtensionException(allowedExtension, extension, fileName);
            } else if (allowedExtension == MimeTypeUtils.FLASH_EXTENSION) {
                throw new InvalidExtensionException.InvalidFlashExtensionException(allowedExtension, extension, fileName);
            } else if (allowedExtension == MimeTypeUtils.MEDIA_EXTENSION) {
                throw new InvalidExtensionException.InvalidMediaExtensionException(allowedExtension, extension, fileName);
            } else if (allowedExtension == MimeTypeUtils.VIDEO_EXTENSION) {
                throw new InvalidExtensionException.InvalidVideoExtensionException(allowedExtension, extension, fileName);
            } else {
                throw new InvalidExtensionException(allowedExtension, extension, fileName);
            }
        }
    }

    /**
     * 判断MIME类型是否是允许的MIME类型
     *
     * @param extension        extension
     * @param allowedExtension allowedExtension
     * @return bool
     */
    public static boolean isAllowedExtension(String extension, String[] allowedExtension) {
        for (String str : allowedExtension) {
            if (str.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取文件名的后缀
     *
     * @param file 表单文件
     * @return 后缀名
     */
    public static String getExtension(MultipartFile file) {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (StrUtil.isEmpty(extension)) {
            extension = MimeTypeUtils.getExtension(Objects.requireNonNull(file.getContentType()));
        }
        return extension;
    }
}
