package cn.ducsr.space.controller;

import cn.ducsr.space.entity.*;
import cn.ducsr.space.service.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 文章控制类
 */
@Controller
@RequestMapping("/article")
public class ArticleController extends BaseController {
    @Resource
    private ArticleService articleService;
    @Resource
    private UserService userService;
    @Resource
    private LogService logService;

    /**
     * 密码文章验证密码
     *
     * @param id       文章ID
     * @param password 输入的密码
     * @param map
     * @return 对应页面
     */
    @RequestMapping(value = "/checkPassword", method = RequestMethod.POST)
    public String checkPassword(Integer id, String password, Map<String, Object> map) {
        // 判断密码是否为空字符串
        if (BaseService.checkNullStr(password))
            return "error";
        // 获取文章信息
        Article article = articleService.findById(id, 1);
        // 文章不存在
        if (article == null) return "error";
        // 加密密码
        password = BaseService.getHash(password.trim(), "MD5");
        // 验证密码是否正确
        if (password.equals(article.getPassword())) {
            map.put("article", article);
            return "article";
        } else {
            article.setTitle("私密文章");
            article.setContent("密码错误，请确认密码是否正确");
            map.put("article", article);
            // 页面根据此字段判断是否该文章是否需要输入密码
            map.put("password", 1);
            return "article";
        }
    }

    /**
     * 为文章设置密码
     *
     * @param id       ID
     * @param password 密码
     * @param request
     * @return JSON
     */
    @ResponseBody
    @RequestMapping(value = "/addPassword", method = RequestMethod.POST)
    public ObjectNode addPassword(Integer id, String password, HttpServletRequest request) {
        // 从cookie中获取登陆用户信息
        User user = userService.getCookieUser(request);
        // 未登录
        if (user == null) {
            objectNode.put("status", 1);
            return objectNode;
        }
        // 密码为空
        if (BaseService.checkNullStr(password)) {
            objectNode.put("status", 1);
            return objectNode;
        }
        // 获取文章信息
        Article article = articleService.findById(id, 1);
        // 判断用户是否有权限操作
        if (userService.checkAuthority(user, article.getAuthor().getId(), 0)) {
            // 加密密码
            article.setPassword(UserService.getHash(password.trim(), "MD5"));
            try {
                // 将操作保存到数据库中
                articleService.save(article);
                objectNode.put("status", 0);
            } catch (Exception e) {
                objectNode.put("status", 1);
            }
        }
        return objectNode;
    }

    /**
     * 从回收站恢复文章
     *
     * @param id      ID
     * @param request
     * @return JSON
     */
    @ResponseBody
    @RequestMapping(value = "/recover", method = RequestMethod.GET)
    public ObjectNode recover(Integer id, HttpServletRequest request) {
        // 从cookie中获取登陆用户信息
        User user = userService.getCookieUser(request);
        // 未登录
        if (user == null) {
            objectNode.put("status", 1);
            return objectNode;
        }
        // 获取文章信息
        Article article = articleService.findById(id, 1);
        // 判断用户是否有权限操作
        if (userService.checkAuthority(user, article.getAuthor().getId(), 0)) {
            // 恢复文章
            article.setTop(0);
            try {
                // 操作保存到数据库中
                articleService.save(article);
                objectNode.put("status", 0);
            } catch (Exception e) {
                objectNode.put("status", 1);
            }
        }
        return objectNode;
    }

    /**
     * 回收站
     *
     * @param page    页码
     * @param size    条数
     * @param request
     * @return 页面
     */
    @RequestMapping(value = "/trash", method = RequestMethod.GET)
    public String trash(Integer page, Integer size, HttpServletRequest request, Map<String, Object> map) {
        // 从cookie中获取登陆用户信息
        User user = userService.getCookieUser(request);
        // 未登录
        if (user == null) return "login";
        int id = user.getId();
        // 分页查询
        if (page == null) page = 1;
        if (size == null) size = 10;
        Page<Article> articlePage = articleService.findTrashByUser(id, page - 1, size);
        // 取出文章列表
        if (articlePage != null) {
            List<Article> articles = articleService.filter(articlePage.getContent());
            // 当页数大于总页数时，查询最后一页的数据
            if (page > articlePage.getTotalPages()) {
                articlePage = articleService.findTrashByUser(id, articlePage.getTotalPages() - 1, size);
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
        } else {
            map.put("articles", null);
        }
        return "trash";
    }

    /**
     * 删除 / 隐藏文章
     *
     * @param request
     * @param id      文章ID
     * @param type    0为隐藏，1为删除
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public ObjectNode delete(Integer id, Integer type, HttpServletRequest request) {
        // 从cookie中获取登陆用户
        User user = userService.getCookieUser(request);
        // 查询文章信息
        Article article = articleService.findById(id, 1);
        switch (type) {
            // 不是真的删除，只是隐藏文章
            case 0:
                if (userService.checkAuthority(user, article.getAuthor().getId(), 0)) {
                    article.setTop(-1);
                    try {
                        articleService.save(article);
                        objectNode.put("status", "0");
                    } catch (Exception e) {
                        objectNode.put("status", "1");
                    }
                }
                break;
            // 真删除
            case 1:
                if (userService.checkAuthority(user, article.getAuthor().getId(), 0)) {
                    try {
                        articleService.delete(id);
                        objectNode.put("status", "0");
                    } catch (Exception e) {
                        objectNode.put("status", "1");
                    }
                    break;
                }
            default:
                objectNode.put("status", 1);
        }
        return objectNode;
    }

    /**
     * 移除置顶 只有最高权限管理员可以做
     *
     * @param request
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/removeTop", method = RequestMethod.GET)
    public ObjectNode removeTop(HttpServletRequest request, Integer id) {
        // 从cookie中获取登陆用户
        User user = userService.getCookieUser(request);
        // 查询文章信息
        Article article = articleService.findById(id, 1);
        // 判断权限
        if (user.getAuthority() > 0)
            objectNode.put("status", "1");
        else {
            // 移除置顶
            article.setTop(0);
            articleService.save(article);
            objectNode.put("status", "0");
        }
        return objectNode;
    }

    /**
     * 设置置顶 只有最高权限管理员可以做
     *
     * @param request
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/setTop", method = RequestMethod.GET)
    public ObjectNode setTop(HttpServletRequest request, Integer id) {
        // 从cookie中获取登陆用户
        User user = userService.getCookieUser(request);
        // 查询文章信息
        Article article = articleService.findById(id, 1);
        // 判断权限
        if (user.getAuthority() > 0)
            objectNode.put("status", "1");
        else {
            // 设置置顶
            if (articleService.setTop(article))
                objectNode.put("status", "0");
            else
                objectNode.put("status", "2");
        }
        return objectNode;
    }

    /**
     * 显示修改文章
     *
     * @param map
     * @param id
     * @return
     */
    @RequestMapping(value = "/updateOne", method = RequestMethod.GET)
    public String updateOne(HttpServletRequest request, Map<String, Object> map, Integer id) {
        // 从cookie中获取登陆用户
        User user = userService.getCookieUser(request);
        Article article;
        if (user != null) {
            // 普通用户无法修改隐藏文章
            if (user.getAuthority() == 0)
                article = articleService.findById(id, 1);
            else
                article = articleService.findById(id, 0);
            // 判断权限
            if (!userService.checkAuthority(user, article.getAuthor().getId(), 0))
                return "login";
            map.put("article", article);
        } else {
            return "login";
        }
        return "update";
    }

    /**
     * 发布文章
     *
     * @param request
     * @param article
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/post", method = RequestMethod.POST)
    public ObjectNode post(HttpServletRequest request, Article article) {
        // 从cookie中获取登陆用户
        User user = userService.getCookieUser(request);
        // 检测是否为空
        if (BaseService.checkNullStr(article.getTitle()) || BaseService.checkNullStr(article.getContent())) {
            // 操作状态保存
            objectNode.put("status", "1");
            return objectNode;
        }
        try {
            // 修改文章
            if (article.getId() != null) {
                Article a;
                if (user.getAuthority() == 0)
                    a = articleService.findById(article.getId(), 1);
                else
                    a = articleService.findById(article.getId(), 0);
                a.setTitle(article.getTitle().trim());
                a.setContent(article.getContent().trim());
                a.setLastChangeTime(new Date());
                articleService.save(a);
            } else {
                article.setTitle(BaseService.killHTML(article.getTitle().trim()));
                article.setContent(article.getContent().trim());
                article.setTime(new Date());
                article.setComments(0);
                article.setLikes(0);
                article.setDislikes(0);
                article.setTop(0);
                article.setViews(0);
                article.setAuthor(user);
                article.setPassword(null);
                article.setLastChangeTime(null);
                articleService.save(article);
            }
            // 操作状态保存
            objectNode.put("status", "0");
        } catch (Exception e) {
            // 操作状态保存
            objectNode.put("status", "1");
        }
        return objectNode;
    }

    /**
     * 获取文章详情
     *
     * @param map 返回页面的数据
     * @param id  ID
     * @return JSON串
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String one(HttpServletRequest request, Map<String, Object> map, @PathVariable("id") Integer id) {
        // 从cookie中获取登陆用户
        User user = userService.getCookieUser(request);
        Article article;
        // 用户无法查看被隐藏的文章
        if (user != null && user.getAuthority() == 0)
            article = articleService.findById(id, 1);
        else
            article = articleService.findById(id, 0);
        if (article == null) return "error";

        if (article.getPassword() == null || article.getPassword().equals(""))
            map.put("article", article);
        else {
            article.setContent("请输入密码后查看");
            map.put("article", article);
            map.put("password", 1);
        }

        // Log
        Log log = new Log();
        // 记录ip
        log.setIp(BaseService.getIpAddress(request));
        log.setUser(user);
        log.setTime(new Date());
        log.setEvent(request.getRequestURL().toString() + "?" + request.getQueryString());
        log.setMethod(request.getServletPath());
        log.setStatus(true);
        logService.save(log);

        return "article";
    }

    /**
     * 查询置顶文章 默认是三个
     *
     * @return 返回置顶文章
     */
    @ResponseBody
    @RequestMapping(value = "/top", method = RequestMethod.GET)
    public List top(Integer size) {
        return articleService.top(size);
    }

    /**
     * 获取文章列表 json 版
     *
     * @param page 当前页数
     * @param size 每页条数
     * @return json串
     */
    @ResponseBody
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public List index(Integer page, Integer size) {
        Page<Article> articlePage = articleService.findAll(page - 1, size, 0);
        if (articlePage != null) {
            List<Article> articles = articleService.filter(articlePage.getContent());
            return articles;
        } else
            return null;
    }

    /**
     * 获取文章列表
     *
     * @param map  返回页面的数据
     * @param page 当前页数
     * @param size 每页条数
     * @return list.html
     */
    @RequestMapping(value = "/list")
    public String list(Map<String, Object> map, Integer page, Integer size) {
        if (page == null) page = 1;
        if (size == null) size = 10;
        // 分页查询
        Page<Article> articlePage = articleService.findAll(page - 1, size, 0);
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
        } else {
            map.put("articles", null);
        }
        return "list";
    }

}
