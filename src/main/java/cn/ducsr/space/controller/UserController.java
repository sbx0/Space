package cn.ducsr.space.controller;

import cn.ducsr.space.entity.User;
import cn.ducsr.space.service.BaseService;
import cn.ducsr.space.service.LogService;
import cn.ducsr.space.service.UserService;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 用户控制类
 */
@Controller
@RequestMapping("/user")
public class UserController extends BaseController {
    @Resource
    private UserService userService;
    @Resource
    private LogService logService;

    /**
     * 获取登陆用户信息
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public ObjectNode info(HttpServletRequest request) {
        String[] names = {"ID", "KEY"};
        Cookie[] cookies = BaseService.getCookiesByName(names, request.getCookies());
        int id = Integer.parseInt(cookies[0].getValue());
        User user = userService.findById(id);
        if (user != null) {
            try {
                // 验证cookie
                if (BaseService.getKey(id).equals(cookies[1].getValue())) {
                    user = userService.findById(user.getId());

                    // 用户信息
                    objectNode.put("username", user.getName());
                    objectNode.put("level", user.getLevel());
                    objectNode.put("exp", user.getExp());
                    objectNode.put("integral", user.getIntegral());

                    // 操作状态保存
                    objectNode.put("status", "0");
                } else {
                    // 操作状态保存
                    objectNode.put("status", "1");
                }
            } catch (Exception e) {
                // 操作状态保存
                objectNode.put("status", "1");
            }
        }
        return objectNode;
    }

    /**
     * 退出登陆
     *
     * @param request
     * @param response
     * @param session
     * @return json
     */
    @ResponseBody
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public ObjectNode logout(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        try {
            // 清除session
            session.removeAttribute("user");
            // 清除Cookie
            String[] names = {"ID", "NAME", "KEY"};
            Cookie[] cookies = BaseService.getCookiesByName(names, request.getCookies());
            BaseService.removeCookies(cookies, response);
            // 操作状态保存
            objectNode.put("status", "0");
        } catch (Exception e) {
            // 操作状态保存
            objectNode.put("status", "1");
        }
        return objectNode;
    }

    /**
     * 获取cookie判断是否之前登陆过
     * 若登陆过将用户信息保存在session中
     *
     * @param request
     * @param session
     * @return json
     */
    @ResponseBody
    @RequestMapping(value = "/cookie", method = RequestMethod.GET)
    public ObjectNode cookie(HttpServletRequest request, HttpSession session) {
        // 查找是否存在cookie
        String[] names = {"ID", "KEY"};
        Cookie[] cookies = BaseService.getCookiesByName(names, request.getCookies());
        try {
            Cookie cookieId = cookies[0];
            Cookie cookieKey = cookies[1];
            if (cookieId.getValue() != null && !cookieId.getValue().equals("")
                    || cookieKey.getValue() != null || !cookieKey.getValue().equals("")) {
                if (BaseService.getKey(Integer.parseInt(cookieId.getValue())).equals(cookieKey.getValue())) {
                    User user = userService.findById(Integer.parseInt(cookieId.getValue()));
                    // 获取session中的user
                    User sessionUser = (User) session.getAttribute("user");
                    if (user != null) {
                        // 操作状态保存
                        objectNode.put("status", "0");
                        if (sessionUser != null && sessionUser.getId() != user.getId())
                            session.setAttribute("user", user);
                    } else {
                        // 操作状态保存
                        objectNode.put("status", "0");
                        // cookie不存在
                        session.removeAttribute("user");
                    }
                } else {
                    objectNode.put("status", "1");
                }
            } else {
                // 操作状态保存
                objectNode.put("status", "1");
            }
        } catch (Exception e) {
            // 操作状态保存
            objectNode.put("status", "1");
        }
        return objectNode;
    }

    /**
     * 登陆注册聚合
     *
     * @param request
     * @param response
     * @param session
     * @param user     表单传递的用户信息
     * @return json
     */
    @ResponseBody
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ObjectNode login(HttpServletRequest request, HttpServletResponse response, HttpSession session, User user) {
        if (user.getName() == null || user.getPassword() == null
                || user.getName().trim().length() == 0 || user.getPassword().trim().length() == 0) {
            // 操作状态保存
            objectNode.put("status", "1");
            return objectNode;
        }
        // 调用登陆，若返回为null，则为密码错误
        user = userService.login(user);
        // 登陆成功
        if (user != null) {
            // 创建Cookie
            response.addCookie(BaseService.createCookie("ID", user.getId() + "", 30));
            response.addCookie(BaseService.createCookie("NAME", user.getName(), 30));
            response.addCookie(BaseService.createCookie("KEY", BaseService.getKey(user.getId()), 30));
            session.setAttribute("user", user);
            // 操作状态保存
            objectNode.put("status", "0");
            // 日志记录
            logService.log(user, request, true);
        } else {
            // 清除Cookie
            String[] names = {"ID", "NAME", "KEY"};
            Cookie[] cookies = BaseService.getCookiesByName(names, request.getCookies());
            BaseService.removeCookies(cookies, response);
            // 操作状态保存
            objectNode.put("status", "1");
            // 日志记录
            logService.log(null, request, false);
        }
        // 返回json串
        return objectNode;
    }

}
