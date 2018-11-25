package cn.sbx0.space.entity;

import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 实体类
 * Url
 */
@Entity
@Table(name = "URLS")
public class Url implements Serializable {
    private static final long serialVersionUID = 407449240992090113L;

    // 那些字段要转成json
    public interface Json {
    }

    @JsonView(Json.class)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // ID
    /**
     * 默认0 不置顶
     * 大于0 时 越大，排序越靠前
     * 小于0 隐藏
     */
    @Column(nullable = false)
    private Integer top; // 置顶
    @JsonView(Json.class)
    @Column(nullable = false)
    private String path; // 路径
    @Column(nullable = false)
    private String page; // 所在页面
    @JsonView(Json.class)
    @Column(nullable = false)
    private String text; // 链接名
    @JsonView(Json.class)
    private String title; // 链接悬浮名
    @JsonView(Json.class)
    private String badge; // 徽章
    private Date begin; // 开始时间
    private Date end; // 结束时间

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTop() {
        return top;
    }

    public void setTop(Integer top) {
        this.top = top;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }

    public Date getBegin() {
        return begin;
    }

    public void setBegin(Date begin) {
        this.begin = begin;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "Url{" +
                "id=" + id +
                ", top=" + top +
                ", path='" + path + '\'' +
                ", page='" + page + '\'' +
                ", text='" + text + '\'' +
                ", title='" + title + '\'' +
                ", badge='" + badge + '\'' +
                ", begin=" + begin +
                ", end=" + end +
                '}';
    }
}
