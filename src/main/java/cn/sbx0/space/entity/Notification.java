package cn.sbx0.space.entity;

import javax.persistence.*;

/**
 * 系统通知类 继承于 消息类
 */
@Entity
@Table(name = "NOTIFICATION")
@PrimaryKeyJoinColumn(name = "notification_id")
public class Notification extends Message {
    private static final long serialVersionUID = 8202981339088982249L;
    @Column(nullable = false, length = 30)
    private String title; // 标题

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "title='" + title + '\'' +
                '}';
    }
}
