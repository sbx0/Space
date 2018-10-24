package cn.sbx0.space.service;

import cn.sbx0.space.dao.UserDao;
import cn.sbx0.space.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * 用户服务层
 */
@Service
public class UserService extends BaseService {
    @Resource
    private UserDao userDao;

    /**
     * 根据id查询用户信息
     *
     * @param id
     * @return
     */
    public User findById(int id) {
        return userDao.findById(id).get();
    }

    /**
     * 保存用户
     *
     * @param user
     * @return 操作成功与否
     */
    @Transactional
    public boolean save(User user) {
        try {
            userDao.save(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 登陆 / 注册
     *
     * @param user
     * @return
     */
    @Transactional
    public User login(User user, String code) {
        user.setName(BaseService.killHTML(user.getName()));
        // 密码加密
        user.setPassword(getHash(user.getPassword(), "MD5"));
        // 用户不存在
        if (!existByName(user.getName())) {
            return null;
//            // 邀请码为空注册失败
//            if (!code.equals("IsPantuPaQiuNa"))
//                return null;
//            // 密码加密
//            user.setRegisterTime(new Date());
//            user.setAuthority(1);
//            user.setLevel(0);
//            user.setIntegral(0.0);
//            user.setExp_max(100.0);
//            user.setExp(0.0);
//            // 注册
//            userDao.save(user);
        }
        // 查询数据库内的用户数据
        User databaseUser = userDao.findByName(user.getName());
        // 密码是否正确
        if (user.getPassword().equals(databaseUser.getPassword())) {
            return databaseUser;
        } else return null;
    }

    // 辅助方法

    /**
     * 判断用户是否有权限操作
     */
    public boolean checkAuthority(User user, int id, int type) {
        if (user == null) return false;
        if (user.getId() == null) return false;
        if (id <= 0) return false;
        if (type < 0) return false;
        switch (type) {
            // 文章
            case 0:
                if (user.getAuthority() == 0) return true;
                if (user.getId() == id) return true;
                break;
            default:
                return false;
        }
        return false;
    }

    /**
     * 根据用户名判断用户是否存在
     *
     * @param name
     * @return
     */
    public boolean existByName(String name) {
        String result = userDao.existsByName(name);
        if (result != null) return true;
        else return false;
    }

    /**
     * 根据cookie查找User
     *
     * @param request
     * @return
     */
    public User getCookieUser(HttpServletRequest request) {
        // 查找是否存在cookie
        Map<String, Cookie> cookies = BaseService.getCookiesByName(COOKIE_NAMES, request.getCookies());
        if (cookies == null) return null;
        if (cookies.size() == 0) return null;
        // 为空
        for (int i = 0; i < cookies.size(); i++) {
            if (BaseService.checkNullStr(cookies.get(COOKIE_NAMES.get(i)).getValue()))
                return null;
        }
        // Cookie中的ID
        int id = Integer.parseInt(cookies.get("ID").getValue());
        // Cookie中的KEY
        String key = cookies.get("KEY").getValue();
        // Cookie中的用户名
        String name = cookies.get("NAME").getValue();
        // 正确的KEY
        String check = BaseService.getKey(id);
        // 匹配KEY
        if (!check.equals(key))
            return null;
        User user = findById(id);
        if (user == null)
            return null;
        if (!user.getName().equals(name))
            return null;
        return user;
    }


}
