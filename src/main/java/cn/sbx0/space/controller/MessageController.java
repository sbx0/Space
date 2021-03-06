package cn.sbx0.space.controller;

import cn.sbx0.space.entity.Message;
import cn.sbx0.space.entity.Notification;
import cn.sbx0.space.entity.User;
import cn.sbx0.space.service.BaseService;
import cn.sbx0.space.service.LogService;
import cn.sbx0.space.service.MessageService;
import cn.sbx0.space.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RequestMapping("/message")
@Controller
public class MessageController extends BaseController<Message, Integer> {
    @Autowired
    MessageService messageService;
    @Resource
    LogService logService;
    @Resource
    UserService userService;
    @Resource
    private SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public BaseService<Message, Integer> getService() {
        return messageService;
    }

    @Autowired
    public MessageController(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * 接收聊天室的消息
     * 仅接收从前天到今天的消息
     */
    @ResponseBody
    @RequestMapping("/receive")
    public ObjectNode receive(String type, HttpServletRequest request) {
        json = mapper.createObjectNode();
        // 从cookie中获取登陆用户信息
        User user = userService.getCookieUser(request);
        // 日志记录
        logService.log(user, request);
        if (BaseService.checkNullStr(type)) {
            json.put(STATUS_NAME, STATUS_CODE_NOT_FOUND);
            return json;
        } else if (type.trim().equals("public")) {
            Date end = new Date();
            end = BaseService.getEndOfDay(end);
            Calendar calendar = Calendar.getInstance(); // 得到日历
            calendar.setTime(end); // 把当前时间赋给日历
            calendar.add(Calendar.DAY_OF_MONTH, -1); // 1天
            Date begin = calendar.getTime();
            begin = BaseService.getStartOfDay(begin);
            List<Message> messages = messageService.findByTime(begin, end, type);
            ArrayNode arrayNode = mapper.createArrayNode();
            for (Message message : messages) {
                json = mapper.createObjectNode();
                if (message.getSendUser() != null) {
                    json.put("u_id", message.getSendUser().getId());
                    json.put("u_name", message.getSendUser().getName());
                }
                DateFormat df = new SimpleDateFormat("HH:mm");
                json.put("send_time", df.format(message.getSendTime()));
                json.put("ip", BaseService.hideFullIp(message.getIp()));
                json.put("content", message.getContent());
                arrayNode.add(json);
            }
            json = mapper.createObjectNode();
            json.set("msgs", arrayNode);
            json.put(STATUS_NAME, STATUS_CODE_SUCCESS);
        } else if (type.trim().equals("notification")) {
            json = mapper.createObjectNode();
            if (user != null && user.getId() != null) {
                // 查询该用户的所有系统通知
                List<Notification> notifications = messageService.findAllByUser(user.getId(), "notification");
                ArrayNode jsons = mapper.createArrayNode();
                for (Notification n : notifications) {
                    ObjectNode notificationNode = mapper.createObjectNode();
                    notificationNode.put("id", n.getId());
                    notificationNode.put("title", n.getTitle());
                    notificationNode.put("content", n.getContent());
                    notificationNode.put("time", n.getSendTime().toString());
                    jsons.add(notificationNode);
                }
                json.set("notifications", jsons);
                json.put(STATUS_NAME, STATUS_CODE_SUCCESS);
            } else {
                json.put(STATUS_NAME, STATUS_CODE_NOT_LOGIN);
            }
            json.put(STATUS_NAME, STATUS_CODE_SUCCESS);
        } else {
            json = mapper.createObjectNode();
            json.put(STATUS_NAME, STATUS_CODE_NOT_FOUND);
        }
        return json;
    }

    /**
     * 发送消息到公共聊天室
     */
    @RequestMapping("/send")
    @ResponseBody
    public ObjectNode send(Message message, String to, HttpServletRequest request) {
        // 防止html注入
        message.setContent(BaseService.killHTML(message.getContent()));
        json = mapper.createObjectNode();
        // 从cookie中获取登陆用户信息
        User user = userService.getCookieUser(request);
        // 检测重复操作
        if (!logService.check(request, 0.1)) {
            json.put(STATUS_NAME, STATUS_CODE_TIMES_LIMIT);
        } else if (BaseService.checkNullStr(to) || BaseService.checkNullStr(message.getContent())) {
            json.put(STATUS_NAME, STATUS_CODE_NOT_FOUND);
        } else if (to.equals("public")) {
            message.setSendTime(new Date());
            message.setSendUser(user);
            message.setType("public");
            String ip = BaseService.getIpAddress(request);
            message.setIp(ip);
            messageService.save(message);
            json.put(STATUS_NAME, STATUS_CODE_SUCCESS);
            simpMessagingTemplate.convertAndSend("/channel/public", MessageService.buildMessage(user, message));
            // 日志记录
            logService.log(user, request);
        }
        return json;
    }

//    /**
//     * 定时推送
//     */
//    @Scheduled(fixedRate = 10000)
//    public void timePush() {
//        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        simpMessagingTemplate.convertAndSend("/channel/public", "<p class=\"chat-notification\">" + df.format(new Date()) + "</p>");
//    }

}
