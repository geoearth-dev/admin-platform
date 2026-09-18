package dev.geo.admin.config.server.model;

import lombok.Getter;
import lombok.Setter;

/**
 * 系统文件相关信息
 *
 * @author qdata
 */
@Getter
@Setter
public class SysFile {

    /**
     * 盘符路径
     */
    private String dirName;

    /**
     * 文件系统类型，如 NTFS、ext4
     */
    private String sysTypeName;

    /**
     * 文件存储名称
     */
    private String typeName;

    /**
     * 总大小
     */
    private String total;

    /**
     * 剩余大小
     */
    private String free;

    /**
     * 已经使用量
     */
    private String used;

    /**
     * 资源的使用率
     */
    private double usage;
}
