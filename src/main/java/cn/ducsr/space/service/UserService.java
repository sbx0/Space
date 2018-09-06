package cn.ducsr.space.service;

import cn.ducsr.space.dao.UserDao;
import cn.ducsr.space.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

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
        // 密码加密
        user.setPassword(getHash(user.getPassword(), "MD5"));
        // 用户不存在 直接注册并登陆
        if (!existByName(user.getName())) {
            // 邀请码为空注册失败
            if (!code.equals("IsPantuPaQiuNa"))
                return null;
            // 密码加密
            user.setRegisterTime(new Date());
            user.setAuthority(1);
            user.setLevel(0);
            user.setIntegral(0.0);
            user.setExp_max(100.0);
            user.setExp(0.0);
            // 注册
            userDao.save(user);
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
        User user = new User();
        String[] names = {"ID", "KEY"};
        Cookie[] cookies = BaseService.getCookiesByName(names, request.getCookies());
        try {
            Cookie cookieId = cookies[0];
            Cookie cookieKey = cookies[1];
            if (cookieId.getValue() != null && !cookieId.getValue().equals("")
                    || cookieKey.getValue() != null || !cookieKey.getValue().equals(""))
                if (BaseService.getKey(Integer.parseInt(cookieId.getValue())).equals(cookieKey.getValue()))
                    user = findById(Integer.parseInt(cookieId.getValue()));
            return user;
        } catch (Exception e) {
            return null;
        }
    }


}
