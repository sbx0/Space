package cn.sbx0.space.controller;

import cn.sbx0.space.entity.Article;
import cn.sbx0.space.entity.Log;
import cn.sbx0.space.entity.User;
import cn.sbx0.space.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

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
    @Resource
    private SimpMessagingTemplate simpMessagingTemplate;
    private final ObjectMapper mapper;

    @Autowired
    public LogController(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * 某个时间段统计访问ip数
     */
    @ResponseBody
    @RequestMapping(value = "/data/views")
    public ArrayNode g2Views() {
        Date now = new Date();
        now = BaseService.getEndOfDay(now);
        Calendar calendar = Calendar.getInstance(); // 得到日历
        calendar.setTime(now); // 把当前时间赋给日历
        calendar.add(Calendar.DAY_OF_MONTH, -30); // 30天
        Date begin = calendar.getTime();
        begin = BaseService.getStartOfDay(begin);
        List<Object[]> views = logService.countIpByTime(begin, now);
        ArrayNode arrayNode = mapper.createArrayNode();
        for (Object[] view : views) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("time", view[0].toString());
            objectNode.put("number", Integer.parseInt(view[1].toString().trim()));
            arrayNode.add(objectNode);
        }
        return arrayNode;
    }

    /**
     * 每天00:00:00 统计昨天的日志
     */
    @Scheduled(cron = "00 00 00 * * ?")
    public void countViews() {
        simpMessagingTemplate.convertAndSend("/channel/public", MessageService.buildMessage("开始统计昨日日志"));
        // 当前时间
        Date end = new Date();
        Calendar calendar = Calendar.getInstance(); //得到日历
        calendar.setTime(end); //把当前时间赋给日历
        calendar.add(Calendar.DAY_OF_MONTH, -1); //设置为前一天
        end = calendar.getTime(); //得到前一天的时间
        // 00:00:00
        Date begin = BaseService.getStartOfDay(end);
        // 23:59:59
        end = BaseService.getEndOfDay(end);
        // 00:00:00 - 23:59:59 之间的日志
        List<Log> logs = logService.countByTime(begin, end);
        simpMessagingTemplate.convertAndSend("/channel/public", MessageService.buildMessage("昨日日志总数：" + logs.size()));
        Log log;
        String method;
        // 存我们想要的日志 其实有点浪费空间
        String[][] store = new String[logs.size()][2];
        for (int i = 0; i < logs.size(); i++) {
            log = logs.get(i);
            // 排除 user id=1 也就是我的账号
            if (log.getUser() != null && log.getUser().getId() == 1) continue;
            method = log.getMethod(); // 例如： /article/list
            method = method.substring(1); // 去除第一个 /
            String[] check = method.split("/"); // 分割成 article list
            if (check[0].equals("article")) {
                store[i][0] = check[0];
                store[i][1] = check[1];
            }
        }
        // 开始计数
        Map<Integer, Integer> count = new HashMap<>();
        for (String[] aStore : store) {
            if (aStore[0] != null) { // 中间有可能有空 暂时 /comment/post 之类的都会排除
                try { // 尝试着把 /article/1 中的 1 取出来
                    int id = Integer.parseInt(aStore[1]);
                    if (count.get(id) != null) { // count中已经有了 删掉加一再放回去
                        int number = count.get(id);
                        count.remove(id);
                        count.put(id, number + 1);
                    } else { // 新建一个
                        count.put(id, 1);
                    }
                } catch (Exception e) {
                    // article/index 之内的不可能可以强制转换成功
                }
            }
        }
        // 开始写入数据库
        for (Map.Entry<Integer, Integer> entry : count.entrySet()) {
            Article article = articleService.findById(entry.getKey(), 1);
            // 文章不存在就...
            if (article == null) continue;
            // 添加阅读数
            int views = article.getViews() + entry.getValue();
            simpMessagingTemplate.convertAndSend("/channel/public", MessageService.buildMessage("文章[" + article.getTitle() + "]+" + entry.getValue() + "=" + views));
            article.setViews(views);
            articleService.save(article);
        }
        simpMessagingTemplate.convertAndSend("/channel/public", MessageService.buildMessage("统计结束"));
        simpMessagingTemplate.convertAndSend("/channel/public", MessageService.buildMessage("晚安"));
    }

    /**
     * 日志列表
     */
    @RequestMapping(value = "/list")
    public String list(Integer page, Integer size, String ip,
                       Map<String, Object> map, HttpServletRequest request) {
        // 从cookie中获取登陆用户
        User user = userService.getCookieUser(request);
        // 只有管理员才可查看
        if (user != null && user.getAuthority() == 0) {
            if (page == null) page = 1;
            if (size == null) size = 50;
            Page<Log> logPage = logService.findAll(page - 1, size, ip);
            if (logPage.getContent().size() > 0) {
                // 将数据返回到页面
                map.put("logs", logPage.getContent());
                if (!BaseService.checkNullStr(ip))
                    map.put("ip", ip);
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
