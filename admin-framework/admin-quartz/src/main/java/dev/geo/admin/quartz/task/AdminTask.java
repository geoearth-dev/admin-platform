package dev.geo.admin.quartz.task;

import org.springframework.stereotype.Component;
import cn.hutool.core.util.StrUtil;

/**
 * 定时任务调度测试
 */
@Component
public class AdminTask {
    public void taskMultipleParams(String s, Boolean b, Long l, Double d, Integer i) {
        System.out.println(StrUtil.format("执行多参方法： 字符串类型{}，布尔类型{}，长整型{}，浮点型{}，整形{}", s, b, l, d, i));
    }

    public void taskParams(String params) {
        System.out.println("执行有参方法：" + params);
    }

    public void taskNoParams() {
        System.out.println("执行无参方法");
    }
}
