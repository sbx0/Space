package cn.sbx0.space.controller;

import cn.sbx0.space.entity.Article;
import cn.sbx0.space.entity.User;
import cn.sbx0.space.service.ArticleService;
import cn.sbx0.space.service.BaseService;
import cn.sbx0.space.service.LogService;
import cn.sbx0.space.service.UserService;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * 用户控制类
 */
@Controller
@RequestMapping("/user")
public class UserController extends BaseController {
    @Resource
    private UserService userService;
    @Resource
    private ArticleService articleService;
    @Resource
    private LogService logService;

    /**
     * 获取用户详情
     *
     * @param map 返回页面的数据
     * @param id  ID
     * @return JSON串
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String one(Map<String, Object> map, @PathVariable("id") Integer id, Integer page, Integer size, HttpServletRequest request) {
        User user = userService.getCookieUser(request);
        if (page == null) page = 1;
        if (size == null) size = 10;
        // 分页查询
        Page<Article> articlePage = articleService.findByUser(id, page - 1, size);
        User u = userService.findById(id);
        if (articlePage != null) {
            // 取出文章列表
            List<Article> articles = articleService.filter(articlePage.getContent());
            // 当页数大于总页数时，查询最后一页的数据
            if (page > articlePage.getTotalPages()) {
                articlePage = articleService.findAll(articlePage.getTotalPages() - 1, size, 0);
                articles = articleService.filter(articlePage.getContent());
            }
            // 将数据返回到页面
            map.put("articles", articles);
            map.put("size", articlePage.getPageable().getPageSize());
            map.put("page", articlePage.getPageable().getPageNumber() + 1);
            map.put("totalPages", articlePage.getTotalPages());
            map.put("totalElements", articlePage.getTotalElements());
            // 判断上下页
            if (page + 1 <= articlePage.getTotalPages()) map.put("next_page", page + 1);
            if (page - 1 > 0) map.put("prev_page", page - 1);
            if (page - 1 > articlePage.getTotalPages()) map.put("prev_page", null);
            if (u == null) {
                u = new User();
                u.setName("该用户不存在");
            }
        } else {
            map.put("articles", null);
        }
        map.put("user", u);

        // 日志记录
        logService.log(user, request);

        return "user";
    }

    /**
     * 获取登陆用户信息
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public ObjectNode info(HttpServletRequest request, HttpServletResponse response) {
        objectNode = mapper.createObjectNode();
        User user = userService.getCookieUser(request);
        if (user.getId() != null) {
            try {
                // 用户信息
                objectNode.put("id", user.getId());
                objectNode.put("username", user.getName());
                objectNode.put("level", user.getLevel());
                objectNode.put("exp", user.getExp());
                objectNode.put("integral", user.getIntegral());
                // 操作状态保存
                objectNode.put("status", "0");
            } catch (Exception e) {
                // 操作状态保存
                objectNode.put("status", "1");
            }
        } else {
            // 清除Cookie
            String[] names = {"ID", "NAME", "KEY"};
            Cookie[] cookies = BaseService.getCookiesByName(names, request.getCookies());
            BaseService.removeCookies(cookies, response);
            // 操作状态保存
            objectNode.put("status", "1");
        }
        return objectNode;
    }

    /**
     * 退出登陆
     *
     * @param request
     * @param response
     * @return json
     */
    @ResponseBody
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public ObjectNode logout(HttpServletRequest request, HttpServletResponse response) {
        objectNode = mapper.createObjectNode();
        try {
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
     * @return json
     */
    @ResponseBody
    @RequestMapping(value = "/cookie", method = RequestMethod.GET)
    public ObjectNode cookie(HttpServletRequest request) {
        objectNode = mapper.createObjectNode();
        User user = userService.getCookieUser(request);
        if (user != null) {
            // 操作状态保存
            objectNode.put("status", "0");
        } else {
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
    public ObjectNode login(User user, String code, HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        objectNode = mapper.createObjectNode();
        // 判断是否为空
        if (BaseService.checkNullStr(user.getName()) || BaseService.checkNullStr(user.getPassword())) {
            // 操作状态保存
            objectNode.put("status", "1");
            return objectNode;
        }
        // 调用登陆，若返回为null，则为密码错误
        user = userService.login(user, code);
        // 登陆成功
        if (user != null) {
            // 创建Cookie
            response.addCookie(BaseService.createCookie("ID", user.getId() + "", 30));
            response.addCookie(BaseService.createCookie("NAME", user.getName(), 30));
            response.addCookie(BaseService.createCookie("KEY", BaseService.getKey(user.getId()), 30));
            session.setAttribute("user", user);
            // 操作状态保存
            objectNode.put("status", "0");
        } else {
            // 清除Cookie
            String[] names = {"ID", "NAME", "KEY"};
            Cookie[] cookies = BaseService.getCookiesByName(names, request.getCookies());
            BaseService.removeCookies(cookies, response);
            // 操作状态保存
            objectNode.put("status", "1");
        }
        // 日志记录
        logService.log(user, request);

        // 返回json串
        return objectNode;
    }

}
