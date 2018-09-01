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

    /**
     * 删除/隐藏文章
     *
     * @param request
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public ObjectNode delete(HttpServletRequest request, Integer id, Integer type) {
        User user = userService.getCookieUser(request);
        Article article = articleService.findById(id, 1);
        // 判断权限
        if (user.getAuthority() > 0 && type == 1) {
            try {
                articleService.delete(id);
                // 操作状态保存
                objectNode.put("status", "0");
            } catch (Exception e) {
                // 操作状态保存
                objectNode.put("status", "1");
            }
        } else if (article.getAuthor().getId() == user.getId() && type == 0) {
            article.setTop(-1);
            try {
                articleService.save(article);
                // 操作状态保存
                objectNode.put("status", "0");
            } catch (Exception e) {
                // 操作状态保存
                objectNode.put("status", "1");
            }
        } else {
            // 操作状态保存
            objectNode.put("status", "1");
        }
        return objectNode;
    }

    /**
     * 移除置顶
     *
     * @param request
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/removeTop", method = RequestMethod.GET)
    public ObjectNode removeTop(HttpServletRequest request, Integer id) {
        User user = userService.getCookieUser(request);
        Article article = articleService.findById(id, 1);
        // 判断权限
        if (user.getAuthority() > 0)
            // 操作状态保存
            objectNode.put("status", "1");
        else {
            article.setTop(0);
            articleService.save(article);
            // 操作状态保存
            objectNode.put("status", "0");
        }
        return objectNode;
    }

    /**
     * 设置置顶
     *
     * @param request
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/setTop", method = RequestMethod.GET)
    public ObjectNode setTop(HttpServletRequest request, Integer id) {
        User user = userService.getCookieUser(request);
        Article article = articleService.findById(id, 1);
        // 判断权限
        if (user.getAuthority() > 0)
            // 操作状态保存
            objectNode.put("status", "1");
        else {
            int max = articleService.getMaxTop();
            article.setTop(max + 1);
            articleService.save(article);
            // 操作状态保存
            objectNode.put("status", "0");
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
        User user = userService.getCookieUser(request);
        Article article;
        if (user.getAuthority() == 0)
            article = articleService.findById(id, 1);
        else
            article = articleService.findById(id, 0);
        // 判断权限
        if (user.getId() != article.getAuthor().getId() && user.getAuthority() > 0)
            return "error";
        map.put("article", article);
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
                article.setTitle(article.getTitle().trim());
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
        User user = userService.getCookieUser(request);
        Article article;
        if (user != null && user.getAuthority() == 0)
            article = articleService.findById(id, 1);
        else
            article = articleService.findById(id, 0);
        if (article == null) {
            article = new Article();
            article.setTitle("该篇文章不存在或已被删除");
            article.setContent("很抱歉。");
        }
        map.put("article", article);

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
        List<Article> articles = articleService.filter(articlePage.getContent());
        return articles;
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
        // 分页查询
        Page<Article> articlePage = articleService.findAll(page - 1, size, 0);
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
        return "list";
    }

}
