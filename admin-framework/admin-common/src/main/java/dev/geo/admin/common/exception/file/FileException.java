package dev.geo.admin.common.exception.file;


import dev.geo.admin.common.exception.base.BaseException;

/**
 * 文件信息异常类
 */
public class FileException extends BaseException {

    public FileException(String code, Object[] args) {
        super("file", code, args, null);
    }

}
