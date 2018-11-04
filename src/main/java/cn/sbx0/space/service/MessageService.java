package cn.sbx0.space.service;

import cn.sbx0.space.dao.MessageDao;
import cn.sbx0.space.entity.Message;
import cn.sbx0.space.entity.User;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 消息服务层
 */
@Service
public class MessageService extends BaseService {
    @Resource
    private MessageDao messageDao;

    /**
     * 构造返回消息的html格式
     *
     * @param user
     * @param message
     * @return
     */
    public static String buildMessage(User user, Message message) {
        String ipOrName;
        if (user != null)
            ipOrName = user.getName();
        else
            ipOrName = BaseService.hideFullIp(message.getIp());
        DateFormat df = new SimpleDateFormat("HH:mm");
        String messageString = "<p class=\"chat-user-name\">" +
                ipOrName + "&nbsp;" +
                "</p><p class=\"chat-receive\">" + df.format(message.getSendTime()) + "："
                + message.getContent() + "</p>";
        return messageString;
    }

    /**
     * 保存
     *
     * @param message
     */
    public void save(Message message) {
        messageDao.save(message);
    }

    /**
     * 某时间段的消息
     *
     * @param begin 开始日期
     * @param end   结束日期
     * @return 日志列表
     */
    public List<Message> findByTime(Date begin, Date end) {
        return messageDao.findByTime(begin, end);
    }

}
