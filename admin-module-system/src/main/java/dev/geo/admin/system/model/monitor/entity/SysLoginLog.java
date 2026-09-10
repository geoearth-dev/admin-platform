package dev.geo.admin.system.model.monitor.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import dev.geo.admin.common.constant.DateTimeFormat;
import dev.geo.admin.excel.annotation.Excel;
import dev.geo.admin.excel.annotation.Excel.ColumnType;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;


/**
 * 系统访问记录表 sys_login_log
 */
@Getter
@Setter
@TableName("sys_login_log")
public class SysLoginLog {

    /**
     * ID
     */
    @Excel(name = "序号", cellType = ColumnType.NUMERIC)
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户账号
     */
    @Excel(name = "用户账号")
    private String userName;

    /**
     * 登录状态 0失败 1成功
     */
    @Excel(name = "登录状态", readConverterExp = "0=失败,1=成功")
    private String status;

    /**
     * 登录IP地址
     */
    @Excel(name = "登录地址")
    private String ipAddress;

    /**
     * 登录地点
     */
    @Excel(name = "登录地点")
    private String loginLocation;

    /**
     * 浏览器类型
     */
    @Excel(name = "浏览器")
    private String browser;

    /**
     * 操作系统
     */
    @Excel(name = "操作系统")
    private String operatingSystem;

    /**
     * 提示消息
     */
    @Excel(name = "提示消息")
    private String message;

    /**
     * 访问时间
     */
    @Excel(name = "访问时间", width = 30, dateFormat = DateTimeFormat.DATE_TIME_PATTERN)
    private Instant loginTime;

}
