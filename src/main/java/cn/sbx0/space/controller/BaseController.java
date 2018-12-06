package cn.sbx0.space.controller;

import org.springframework.stereotype.Controller;

@Controller
public class BaseController {
    public static final String STATUS_NAME = "status"; // JSON返回状态字段名
    // JSON返回操作状态一览 Begin
    public static final int STATUS_CODE_SUCCESS = 0; // 成功
    public static final int STATUS_CODE_FILED = 1; // 失败
    public static final int STATUS_CODE_EXCEPTION = 2; // 异常
    public static final int STATUS_CODE_NOT_LOGIN = 3; // 未登录
    public static final int STATUS_CODE_TIMES_LIMIT = 4; // 限制
    public static final int STATUS_CODE_NOT_FOUND = 5; // 不存在
    public static final int STATUS_CODE_NO_PERMISSION = 6; // 无权限
    public static final int STATUS_CODE_REPEAT = 7; // 重复操作
    // JSON返回操作状态一览 End
}
