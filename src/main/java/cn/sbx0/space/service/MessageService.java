package cn.sbx0.space.service;

import cn.sbx0.space.dao.MessageDao;
import cn.sbx0.space.dao.NotificationDao;
import cn.sbx0.space.entity.Message;
import cn.sbx0.space.entity.Notification;
import cn.sbx0.space.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
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
public class MessageService extends BaseService<Message, Integer> {
    @Resource
    private MessageDao messageDao;

    @Override
    public PagingAndSortingRepository<Message, Integer> getDao() {
        return messageDao;
    }

    @Resource
    private NotificationDao notificationDao;


    /**
     * 根据用户查询消息
     */
    public List<Notification> findAllByUser(Integer u_id, String type) {
        type = "notification";
        // 分页配置
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(0, 10, sort);
        Page<Notification> notifications = notificationDao.findAllByUser(u_id, type, pageable);
        return notifications.getContent();
    }

    /**
     * 构造通知消息的html格式
     */
    public static String buildMessage(String message) {
        return "<p class=\"chat-notification\">" +
                message.trim() +
                "</p>";
    }

    /**
     * 构造返回消息的html格式
     */
    public static String buildMessage(User user, Message message) {
        String ipOrName;
        if (user != null)
            ipOrName = user.getName();
        else
            ipOrName = BaseService.hideFullIp(message.getIp());
        DateFormat df = new SimpleDateFormat("HH:mm");
        return "<p class=\"chat-user-name\">" +
                ipOrName + "&nbsp;" +
                "</p><p class=\"chat-receive\">" + df.format(message.getSendTime()) + "："
                + message.getContent() + "</p>";
    }

    /**
     * 某时间段的消息
     */
    public List<Message> findByTime(Date begin, Date end, String type) {
        return messageDao.findByTime(begin, end, type);
    }

}
