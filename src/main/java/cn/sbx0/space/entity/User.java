package cn.sbx0.space.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 代码 2
 * 用户
 */
@Entity
@Table(name = "USERS")
public class User implements Serializable {
    private static final long serialVersionUID = 1389714315239886773L;
    @JsonView(Article.Top.class)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @JsonView(Article.Index.class)
    @Column(unique = true, nullable = false, length = 30)
    private String name; // 名称
    @Column(nullable = false, length = 40)
    private String password; // 密码
    @Column(length = 60)
    private String signature; //  签名
    @Column(nullable = false)
    private Date registerTime; // 注册时间
    private Date birthday; // 生日
    @Column(nullable = false, columnDefinition = "Decimal(10,1) default '0.0'")
    private Double integral; // 积分
    /**
     * authority
     * 若大于0 表示为普通用户，数值越大（注意是正数，越靠近0权限越小）用户可操作的东西越多
     * 若等于0 为最高权限，可操作任何东西
     * 若小于0 表示为管理员，数值越大（注意是负数，越靠近0权限越大）用户可操作的东西越多
     */
    @JsonIgnore
    @Column(nullable = false)
    private Integer authority; // 权限 默认普通用户
    @Column(nullable = false)
    private Integer level; // 用户等级
    @Column(nullable = false, columnDefinition = "Decimal(10,1) default '0.0'")
    private double exp; // 用户经验
    @Column(nullable = false, columnDefinition = "Decimal(10,1) default '100.0'")
    private double exp_max; // 当前等级的最大经验值，超过清空升级

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", signature='" + signature + '\'' +
                ", registerTime=" + registerTime +
                ", birthday=" + birthday +
                ", integral=" + integral +
                ", authority=" + authority +
                ", level=" + level +
                ", exp=" + exp +
                ", exp_max=" + exp_max +
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public Date getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(Date registerTime) {
        this.registerTime = registerTime;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Double getIntegral() {
        return integral;
    }

    public void setIntegral(Double integral) {
        this.integral = integral;
    }

    public Integer getAuthority() {
        return authority;
    }

    public void setAuthority(Integer authority) {
        this.authority = authority;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public double getExp() {
        return exp;
    }

    public void setExp(double exp) {
        this.exp = exp;
    }

    public double getExp_max() {
        return exp_max;
    }

    public void setExp_max(double exp_max) {
        this.exp_max = exp_max;
    }

}
