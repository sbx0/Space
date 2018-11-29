package cn.sbx0.space.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "BUGS")
public class Bug implements Serializable {
    private static final long serialVersionUID = -739048230235409854L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private String name; // B123456
    @Column(nullable = false)
    private String content; // 描述
    @Column(nullable = false)
    private Integer grade; // 分级 0 1 2 3 4 建议 低级 一般 严重 致命
    @Column(nullable = false)
    private Integer status; // 状态 -1 已修复 0 新提交 1 解决中 2 退回 3
    private String environment; // 运行环境 一般自动填充
    @Column(nullable = false)
    private String ip;
    @ManyToOne(cascade = {CascadeType.MERGE}, fetch = FetchType.EAGER, targetEntity = User.class)
    private User sumbitter; // 提交者
    @ManyToOne(cascade = {CascadeType.MERGE}, fetch = FetchType.EAGER, targetEntity = User.class)
    private User solver; // 解决者
    @Column(nullable = false)
    private Date submitTime; // 提交时间
    private Date solvedTime; // 解决时间
    @Column(nullable = false)
    private Integer top; // 用于置顶 或 隐藏 或 自定义排序

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public User getSumbitter() {
        return sumbitter;
    }

    public void setSumbitter(User sumbitter) {
        this.sumbitter = sumbitter;
    }

    public User getSolver() {
        return solver;
    }

    public void setSolver(User solver) {
        this.solver = solver;
    }

    public Date getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(Date submitTime) {
        this.submitTime = submitTime;
    }

    public Date getSolvedTime() {
        return solvedTime;
    }

    public void setSolvedTime(Date solvedTime) {
        this.solvedTime = solvedTime;
    }

    public Integer getTop() {
        return top;
    }

    public void setTop(Integer top) {
        this.top = top;
    }

    @Override
    public String toString() {
        return "Bug{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", content='" + content + '\'' +
                ", grade=" + grade +
                ", status=" + status +
                ", environment='" + environment + '\'' +
                ", ip='" + ip + '\'' +
                ", sumbitter=" + sumbitter +
                ", solver=" + solver +
                ", submitTime=" + submitTime +
                ", solvedTime=" + solvedTime +
                ", top=" + top +
                '}';
    }
}
