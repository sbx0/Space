package cn.ducsr.space.controller;

import cn.ducsr.space.entity.Article;
import cn.ducsr.space.entity.User;
import cn.ducsr.space.service.ArticleService;
import cn.ducsr.space.service.BaseService;
import cn.ducsr.space.service.LogService;
import cn.ducsr.space.service.UserService;
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

    @ResponseBody
    @RequestMapping(value = "/post", method = RequestMethod.POST)
    public ObjectNode post(HttpServletRequest request, Article article) {
        String[] names = {"ID"};
        Cookie[] cookies = BaseService.getCookiesByName(names, request.getCookies());
        int id = Integer.parseInt(cookies[0].getValue());
        User user = userService.findById(id);
//        try {
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
            // 操作状态保存
            objectNode.put("status", "0");
//        } catch (Exception e) {
//            // 操作状态保存
//            objectNode.put("status", "1");
//        }
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
        Article article = articleService.one(id);
        if (article == null) {
            article = new Article();
            article.setTitle("该篇文章不存在或已被删除");
            article.setContent("很抱歉。");
        }
        map.put("article", article);

        // 日志记录
        logService.log(null, request, true);

        return "article";
    }

    /**
     * 查询置顶文章 默认是三个
     *
     * @return 返回置顶文章
     */
    @ResponseBody
    @RequestMapping(value = "/top", method = RequestMethod.GET)
    public List top(HttpServletRequest request, Integer size) {

        // 日志记录
        logService.log(null, request, true);

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
    public List index(HttpServletRequest request, Integer page, Integer size) {
        Page<Article> articlePage = articleService.findAll(page - 1, size);
        List<Article> articles = articleService.filter(articlePage.getContent());

        // 日志记录
        logService.log(null, request, true);

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
    public String list(HttpServletRequest request, Map<String, Object> map, Integer page, Integer size) {
        // 分页查询
        Page<Article> articlePage = articleService.findAll(page - 1, size);
        // 取出文章列表
        List<Article> articles = articleService.filter(articlePage.getContent());
        // 当页数大于总页数时，查询最后一页的数据
        if (page > articlePage.getTotalPages()) {
            articlePage = articleService.findAll(articlePage.getTotalPages() - 1, size);
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

        // 日志记录
        logService.log(null, request, true);

        return "list";
    }

    /**
     * 保存文章
     *
     * @return 字符串
     */
    @RequestMapping(value = "/save")
    public String save(HttpServletRequest request) {
        Article article = new Article();
        article.setTitle("标题");
        article.setContent("内容");
        article.setTime(new Date());
        article.setLastChangeTime(new Date());
        articleService.save(article);

        // 日志记录
        logService.log(null, request, true);

        return "success";
    }

}
