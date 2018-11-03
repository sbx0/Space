package cn.sbx0.space.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 代码 3
 * 评论实体类
 */
@Entity
@Table(name = "COMMENTS")
public class Comment implements Serializable {
    private static final long serialVersionUID = 2325996491164098528L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer user_id; // 用户ID
    @Column(nullable = false, length = 30)
    private String user_name; // 用户名称
    @Column(length = 100)
    private String user_ip; // 用户IP
    @Column(nullable = false)
    private Integer floor; // 楼层
    @Column(nullable = false, length = 999)
    private String content; // 评论
    @Column(nullable = false)
    private Date time; // 时间
    private Date lastChangeTime; // 上次修改时间
    @Column(nullable = false)
    private Integer likes; // 喜欢数
    @Column(nullable = false)
    private Integer dislikes; // 不喜欢数
    @Column(nullable = false)
    private String entity_type; // 对应实体类
    @Column(nullable = false)
    private Integer entity_id; // 对应实体类ID
    /**
     * 默认0 不置顶
     * 大于0 越大，排序越靠前
     * 小于0 隐藏
     */
    @Column(nullable = false)
    private Integer top; // 置顶排序

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", user_id=" + user_id +
                ", user_name='" + user_name + '\'' +
                ", user_ip='" + user_ip + '\'' +
                ", floor=" + floor +
                ", content='" + content + '\'' +
                ", time=" + time +
                ", lastChangeTime=" + lastChangeTime +
                ", likes=" + likes +
                ", dislikes=" + dislikes +
                ", entity_type='" + entity_type + '\'' +
                ", entity_id=" + entity_id +
                ", top=" + top +
                '}';
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_ip() {
        return user_ip;
    }

    public void setUser_ip(String user_ip) {
        this.user_ip = user_ip;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Date getLastChangeTime() {
        return lastChangeTime;
    }

    public void setLastChangeTime(Date lastChangeTime) {
        this.lastChangeTime = lastChangeTime;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public Integer getDislikes() {
        return dislikes;
    }

    public void setDislikes(Integer dislikes) {
        this.dislikes = dislikes;
    }

    public String getEntity_type() {
        return entity_type;
    }

    public void setEntity_type(String entity_type) {
        this.entity_type = entity_type;
    }

    public Integer getEntity_id() {
        return entity_id;
    }

    public void setEntity_id(Integer entity_id) {
        this.entity_id = entity_id;
    }

    public Integer getTop() {
        return top;
    }

    public void setTop(Integer top) {
        this.top = top;
    }

}
