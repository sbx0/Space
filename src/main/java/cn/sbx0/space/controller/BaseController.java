package cn.sbx0.space.controller;

import org.springframework.stereotype.Controller;

@Controller
public class BaseController {
    public static final String STATUS_NAME = "status"; // JSON返回状态字段名
    // JSON返回操作状态一览 Begin
    public static final int SUCCESS_STATUS_CODE = 0; // 成功
    public static final int FILED_STATUS_CODE = 1; // 失败
    public static final int EXCEPTION_STATUS_CODE = 2; // 异常
    public static final int NOT_LOGIN_STATUS_CODE = 3; // 未登录
    public static final int TIMES_LIMIT_STATUS_CODE = 4; // 限制
    public static final int NOT_FOUND_STATUS_CODE = 5; // 不存在
    public static final int NO_PERMISSION_STATUS_CODE = 6; // 无权限
    // JSON返回操作状态一览 End
}
