package cn.sbx0.space.controller;

import cn.sbx0.space.entity.Message;
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
public class MessageController {
    @Resource
    private SimpMessagingTemplate simpMessagingTemplate;
    @Resource
    LogService logService;
    @Resource
    UserService userService;
    @Resource
    MessageService messageService;
    @Autowired
    ObjectMapper mapper;
    private ObjectNode objectNode;

    /**
     * 接收聊天室的消息
     *
     * @param request
     * @return
     */
    @RequestMapping("/receive")
    @ResponseBody
    public ArrayNode receive(HttpServletRequest request) {
        objectNode = mapper.createObjectNode();
        // 从cookie中获取登陆用户信息
        User user = userService.getCookieUser(request);
        // 日志记录
        logService.log(user, request);
        Date end = new Date();
        end = BaseService.getEndOfDay(end);
        Calendar calendar = Calendar.getInstance(); // 得到日历
        calendar.setTime(end); // 把当前时间赋给日历
        calendar.add(Calendar.DAY_OF_MONTH, -1); // 1天
        Date begin = calendar.getTime();
        begin = BaseService.getStartOfDay(begin);
        List<Message> messages = messageService.findByTime(begin, end);
        ArrayNode arrayNode = mapper.createArrayNode();
        for (Message message : messages) {
            objectNode = mapper.createObjectNode();
            if (message.getSendUser() != null) {
                objectNode.put("u_id", message.getSendUser().getId());
                objectNode.put("u_name", message.getSendUser().getName());
            }
            DateFormat df = new SimpleDateFormat("HH:mm");
            objectNode.put("send_time", df.format(message.getSendTime()));
            objectNode.put("ip", BaseService.hideFullIp(message.getIp()));
            objectNode.put("content", message.getContent());
            arrayNode.add(objectNode);
        }
        return arrayNode;
    }

    /**
     * 发送消息到公共聊天室
     *
     * @param message
     * @param to
     * @param request
     * @return
     */
    @RequestMapping("/send")
    @ResponseBody
    public ObjectNode send(Message message, String to, HttpServletRequest request) {
        // 防止html注入
        message.setContent(BaseService.killHTML(message.getContent()));
        objectNode = mapper.createObjectNode();
        // 从cookie中获取登陆用户信息
        User user = userService.getCookieUser(request);
        // 检测重复操作
        if (!logService.check(request, 0.1)) {
            objectNode.put("status", 1);
        } else if (BaseService.checkNullStr(to) || BaseService.checkNullStr(message.getContent())) {
            objectNode.put("status", 2);
        } else if (to.equals("public")) {
            message.setSendTime(new Date());
            message.setSendUser(user);
            message.setType("public");
            String ip = BaseService.getIpAddress(request);
            message.setIp(ip);
            messageService.save(message);
            objectNode.put("status", 0);
            DateFormat df = new SimpleDateFormat("HH:mm");
            if (user != null)
                simpMessagingTemplate.convertAndSend("/channel/public",
                        "<p class=\"chat-user-name\">" +
                                user.getName() + "&nbsp;" +
                                "</p><p class=\"chat-receive\">" + df.format(message.getSendTime()) + "："
                                + message.getContent() + "</p>");
            else
                simpMessagingTemplate.convertAndSend("/channel/public", "<p class=\"chat-user-name\">"
                        + BaseService.hideFullIp(message.getIp()) + "&nbsp;" + df.format(message.getSendTime()) +
                        "</p><p class=\"chat-receive\">" + df.format(message.getSendTime()) + "："
                        + message.getContent() + "</p>");
            // 日志记录
            logService.log(user, request);
        }
        return objectNode;
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
