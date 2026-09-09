package dev.geo.admin.common.exception.file;

/**
 * 文件名大小限制异常类
 * 
 */
public class FileSizeLimitExceededException extends FileException
{

    public FileSizeLimitExceededException(long defaultMaxSize)
    {
        super("upload.exceed.maxSize", new Object[] { defaultMaxSize });
    }
}
