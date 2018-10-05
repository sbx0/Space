package cn.ducsr.space.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * 代码 1
 * 文章实体类
 */
@Entity
@Table(name = "ARTICLES")
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false, length = 30)
    private String title; // 标题
    @Column(nullable = false)
    private Date time; // 时间
    @Lob
    @Column(nullable = false)
    private String content; // 内容
    private Date lastChangeTime; // 上次修改时间
    @Column(length = 40)
    private String password; // 密码
    @Column(nullable = false)
    private Integer views; // 查看数
    @Column(nullable = false)
    private Integer comments; // 评论数
    @Column(nullable = false)
    private Integer likes; // 喜欢数
    @Column(nullable = false)
    private Integer dislikes; // 不喜欢数
    /**
     * 默认0 不置顶
     * 大于0 时 越大，排序越靠前
     * 小于0 隐藏
     */
    @Column(nullable = false)
    private Integer top; // 置顶排序
    /**
     * @ManyToOne 使用此标签建立多对一关联，此属性在“多”方使用注解在我们的“一”方属性上
     * @cascade 指定级联操作，以数组方式指定，如果只有一个，可以省略“{}”
     * @fetch 定义抓取策略
     * @optional 定义是否为必需属性，如果为必需（false），但在持久化时user = null,则会持久化失败
     * @targetEntity 目标关联对象，默认为被注解属性所在类
     */
    @ManyToOne(cascade = {CascadeType.MERGE}, fetch = FetchType.EAGER, targetEntity = User.class, optional = false)
    private User author; // 作者

    // Set Get Begin

    public Integer getTop() {
        return top;
    }

    public void setTop(Integer top) {
        this.top = top;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title.length() > 30) title = title.substring(0, 30);
        this.title = title.trim();
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content.trim();
    }

    public Date getLastChangeTime() {
        return lastChangeTime;
    }

    public void setLastChangeTime(Date lastChangeTime) {
        this.lastChangeTime = lastChangeTime;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getViews() {
        return views;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    public Integer getComments() {
        return comments;
    }

    public void setComments(Integer comments) {
        this.comments = comments;
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

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    // Set Get End

    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", time=" + time +
                ", content='" + content + '\'' +
                ", lastChangeTime=" + lastChangeTime +
                ", password='" + password + '\'' +
                ", views=" + views +
                ", comments=" + comments +
                ", likes=" + likes +
                ", dislikes=" + dislikes +
                ", top=" + top +
                ", author=" + author +
                '}';
    }
}
