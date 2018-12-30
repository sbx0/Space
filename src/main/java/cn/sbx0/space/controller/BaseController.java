package cn.sbx0.space.controller;

import cn.sbx0.space.service.BaseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public abstract class BaseController<T, ID> {
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
    protected ObjectMapper mapper;
    protected ObjectNode json;

    public abstract BaseService<T, ID> getService();

    @InitBinder
    public void initBinder(WebDataBinder binder, WebRequest request) {
        //转换日期 注意这里的转化要和传进来的字符串的格式一直 如2015-9-9 就应该为yyyy-MM-dd
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));// CustomDateEditor为自定义日期编辑器
    }

    @ResponseBody
    @GetMapping("/findById")
    public T findById(ID id) {
        T t = getService().findById(id);
        return t;
    }

    /**
     * 增 改
     */
    @ResponseBody
    @PostMapping("/add")
    public ObjectNode add(T t) {
        json = mapper.createObjectNode();
        try {
            if (getService().save(t)) {
                json.put(STATUS_NAME, STATUS_CODE_SUCCESS);
            } else {
                json.put(STATUS_NAME, STATUS_CODE_FILED);
            }
        } catch (Exception e) {
            json.put(STATUS_NAME, STATUS_CODE_EXCEPTION);
            json.put("msg", e.getMessage());
        }
        return json;
    }

    @ResponseBody
    @GetMapping("/delete")
    public ObjectNode delete(ID id) {
        json = mapper.createObjectNode();
        try {
            if (getService().deleteById(id)) {
                json.put(STATUS_NAME, STATUS_CODE_SUCCESS);
            } else {
                json.put(STATUS_NAME, STATUS_CODE_FILED);
            }
        } catch (Exception e) {
            json.put(STATUS_NAME, STATUS_CODE_EXCEPTION);
            json.put("msg", e.getMessage());
        }
        return json;
    }
}
