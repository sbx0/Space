package cn.sbx0.space.controller;

import cn.sbx0.space.entity.Log;
import cn.sbx0.space.entity.User;
import cn.sbx0.space.service.LogService;
import cn.sbx0.space.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 日志控制类
 */
@Controller
@RequestMapping("/log")
public class LogController extends BaseController {
    @Resource
    LogService logService;
    @Resource
    UserService userService;

    /**
     * 日志列表
     *
     * @param page
     * @param size
     * @param map
     * @return
     */
    @RequestMapping(value = "/list")
    public String list(Integer page, Integer size, Map<String, Object> map, HttpServletRequest request) {
        // 从cookie中获取登陆用户
        User user = userService.getCookieUser(request);
        // 只有管理员才可查看
        if (user != null && user.getAuthority() == 0) {
            if (page == null) page = 1;
            if (size == null) size = 50;
            Page<Log> logPage = logService.findAll(page - 1, size);
            if (logPage.getContent().size() > 0) {
                // 将数据返回到页面
                map.put("logs", logPage.getContent());
                map.put("size", logPage.getPageable().getPageSize());
                map.put("page", logPage.getPageable().getPageNumber() + 1);
                map.put("totalPages", logPage.getTotalPages());
                map.put("totalElements", logPage.getTotalElements());
                // 判断上下页
                if (page + 1 <= logPage.getTotalPages()) map.put("next_page", page + 1);
                if (page - 1 > 0) map.put("prev_page", page - 1);
                if (page - 1 > logPage.getTotalPages()) map.put("prev_page", null);
            } else {
                map.put("logs", null);
            }
        } else {
            map.put("logs", null);
        }
        return "log";
    }
}
