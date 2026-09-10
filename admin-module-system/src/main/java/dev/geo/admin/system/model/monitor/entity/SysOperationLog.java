package dev.geo.admin.system.model.monitor.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import dev.geo.admin.common.constant.DateTimeFormat;
import dev.geo.admin.excel.annotation.Excel;
import dev.geo.admin.excel.annotation.Excel.ColumnType;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;


/**
 * 操作日志记录表 oper_log
 */
@Setter
@Getter
@TableName("sys_operation_log")
public class SysOperationLog {

    /**
     * 日志主键
     */
    @Excel(name = "操作序号", cellType = ColumnType.NUMERIC)
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 操作模块
     */
    @Excel(name = "操作模块")
    private String title;

    /**
     * 业务类型（0其它 1新增 2修改 3删除）
     */
    @Excel(name = "业务类型", readConverterExp = "0=其它,1=新增,2=修改,3=删除,4=授权,5=导出,6=导入,7=强退,8=生成代码,9=清空数据")
    private Integer businessType;

    /**
     * 业务类型数组
     */
    @TableField(exist = false)
    private Integer[] businessTypes;

    /**
     * 请求方法
     */
    @Excel(name = "请求方法")
    private String methodName;

    /**
     * 请求方式
     */
    @Excel(name = "请求方式")
    private String httpMethod;

    /**
     * 操作类别（0其它 1后台用户 2手机端用户）
     */
    @Excel(name = "操作类别", readConverterExp = "0=其它,1=后台用户,2=手机端用户")
    private Integer operatorType;

    /**
     * 操作人员
     */
    @Excel(name = "操作人员")
    private String userName;

    /**
     * 部门名称
     */
    @Excel(name = "部门名称")
    private String deptName;

    /**
     * 请求url
     */
    @Excel(name = "请求地址")
    private String requestUri;

    /**
     * 操作地址
     */
    @Excel(name = "操作地址")
    private String ipAddress;

    /**
     * 操作地点
     */
    @Excel(name = "操作地点")
    private String operationLocation;

    /**
     * 请求参数
     */
    @Excel(name = "请求参数")
    private String requestParams;

    /**
     * 返回参数
     */
    @Excel(name = "返回参数")
    private String responseBody;

    /**
     * 操作状态（0异常 1正常）
     */
    @Excel(name = "状态", readConverterExp = "0=异常,1=正常")
    private Integer status;

    /**
     * 错误消息
     */
    @Excel(name = "错误消息")
    private String errorMessage;

    /**
     * 操作时间
     */
    @Excel(name = "操作时间", width = 30, dateFormat = DateTimeFormat.DATE_TIME_PATTERN)
    private Instant operationTime;

    /**
     * 消耗时间
     */
    @Excel(name = "消耗时间", suffix = "毫秒")
    private Long durationMs;

}
