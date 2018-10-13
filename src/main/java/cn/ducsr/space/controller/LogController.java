package cn.ducsr.space.controller;

import cn.ducsr.space.entity.Log;
import cn.ducsr.space.service.LogService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 日志控制类
 */
@Controller
@RequestMapping("/log")
public class LogController extends BaseController {
    @Resource
    LogService logService;

    /**
     * 日志列表
     *
     * @param page
     * @param size
     * @param map
     * @return
     */
    @RequestMapping(value = "/list")
    public String list(Integer page, Integer size, Map<String, Object> map) {
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
        return "log";
    }
}
