package cn.sbx0.space.controller;

import cn.sbx0.space.entity.Article;
import cn.sbx0.space.entity.Log;
import cn.sbx0.space.entity.User;
import cn.sbx0.space.service.ArticleService;
import cn.sbx0.space.service.BaseService;
import cn.sbx0.space.service.LogService;
import cn.sbx0.space.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
    @Resource
    ArticleService articleService;

    /**
     * 每天23:59:59秒 根据今天的日志统计日志
     */
    @Scheduled(cron = "59 59 23 * * ?")
    public void countViews() {
        // 当前时间为截至时间
        Date end = new Date();
        Date begin = BaseService.getStartOfDay(end);
        end = BaseService.getEndOfDay(end);
        List<Log> logs = logService.countByTime(begin, end);
        Log log;
        String method;
        String[][] store = new String[logs.size()][2];
        for (int i = 0; i < logs.size(); i++) {
            log = logs.get(i);
            method = log.getMethod();
            method = method.substring(1);
            String[] check = method.split("/");
            if (check[0].equals("article")) {
                store[i][0] = check[0];
                store[i][1] = check[1];
            }
        }
        Map<Integer, Integer> count = new HashMap<>();
        for (int i = 0; i < store.length; i++) {
            if (store[i][0] != null) {
                try {
                    int id = Integer.parseInt(store[i][1]);
                    if (count.get(id) != null) {
                        int number = count.get(id);
                        count.remove(id);
                        count.put(id, number + 1);
                    } else {
                        count.put(id, 1);
                    }
                } catch (Exception e) {
                    continue;
                }
            }
        }
        for (Map.Entry<Integer, Integer> entry : count.entrySet()) {
            Article article = articleService.findById(entry.getKey(), 1);
            if (article == null) continue;
            int views = article.getViews() + entry.getValue();
            article.setViews(views);
            articleService.save(article);
        }
    }

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
