package cn.ducsr.space.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * 日志实体类
 */
@Entity
@Table(name = "LOGS")
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false, length = 30)
    private String ip; // IP
    @ManyToOne(cascade = {CascadeType.MERGE}, fetch = FetchType.EAGER, targetEntity = User.class)
    private User user; // 人物
    @Column(nullable = false)
    private Date time; // 时间
    @Column(length = 100)
    private String event; // 事件
    @Column(nullable = false, length = 100)
    private String method; // 方法
    @Column(nullable = false)
    private boolean status; // 状态

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
