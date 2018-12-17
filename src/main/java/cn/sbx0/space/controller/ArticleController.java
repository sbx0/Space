package cn.sbx0.space.controller;

import cn.sbx0.space.entity.*;
import cn.sbx0.space.service.*;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 文章控制类
 */
@Controller
@RequestMapping("")
public class ArticleController extends BaseController {
    @Resource
    private ArticleService articleService;
    @Resource
    private UserService userService;
    @Resource
    private LogService logService;
    private ObjectMapper mapper;
    private ObjectNode json;

    @Autowired
    public ArticleController(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * 文章排行
     */
    @ResponseBody
    @RequestMapping(value = "/article/rank", method = RequestMethod.GET)
    public ArrayNode rank(Integer days, Integer size) {
        ArrayNode articles = mapper.createArrayNode();
        List<Article> articleList = articleService.rank(days, size);
        for (int i = 0; i < articleList.size(); i++) {
            json = mapper.createObjectNode();
        }
        return articles;
    }

    /**
     * 给文章点踩
     */
    @ResponseBody
    @RequestMapping(value = "/article/dislike", method = RequestMethod.GET)
    public ObjectNode dislike(Integer id, HttpServletRequest request) {
        json = mapper.createObjectNode();
        // 检测重复操作
        if (!logService.check(request, 1440)) {
            json.put(STATUS_NAME, STATUS_CODE_TIMES_LIMIT);
        } else {
            // 日志记录
            logService.log(userService.getCookieUser(request), request);
            Article article = articleService.findById(id, 0);
            if (article != null) {
                try {
                    int dislikes = article.getDislikes();
                    if (dislikes < 0) dislikes = 0;
                    dislikes++;
                    if (dislikes + 1 > Integer.MAX_VALUE - 5) {
                        dislikes--;
                    }
                    article.setDislikes(dislikes);
                    articleService.save(article);
                    json.put("dislikes", article.getDislikes());
                    json.put(STATUS_NAME, STATUS_CODE_SUCCESS);
                } catch (Exception e) {
                    json.put("msg", e.getMessage());
                    json.put(STATUS_NAME, STATUS_CODE_EXCEPTION);
                }
            } else {
                json.put(STATUS_NAME, STATUS_CODE_NOT_FOUND);
            }
        }
        return json;
    }

    /**
     * 给文章点赞
     */
    @ResponseBody
    @RequestMapping(value = "/article/like", method = RequestMethod.GET)
    public ObjectNode like(Integer id, HttpServletRequest request) {
        json = mapper.createObjectNode();
        // 检测重复操作
        if (!logService.check(request, 1440)) {
            json.put(STATUS_NAME, STATUS_CODE_TIMES_LIMIT);
        } else {
            // 日志记录
            logService.log(userService.getCookieUser(request), request);
            Article article = articleService.findById(id, 0);
            if (article != null) {
                try {
                    int likes = article.getLikes();
                    if (likes < 0) likes = 0;
                    likes++;
                    if (likes + 1 > Integer.MAX_VALUE - 5) {
                        likes--;
                    }
                    article.setLikes(likes);
                    articleService.save(article);
                    json.put("likes", article.getLikes());
                    json.put(STATUS_NAME, STATUS_CODE_SUCCESS);
                } catch (Exception e) {
                    json.put("msg", e.getMessage());
                    json.put(STATUS_NAME, STATUS_CODE_EXCEPTION);
                }
            } else {
                json.put(STATUS_NAME, STATUS_CODE_NOT_FOUND);
            }
        }
        return json;
    }

    /**
     * 查询上一条下一条
     * 如果u_id不为空 查询的是该用户的上一篇下一篇博文
     * 如果u_id为空 查询的是不限用户的上一篇下一篇
     */
    @ResponseBody
    @RequestMapping(value = "/article/prevAndNext", method = RequestMethod.GET)
    public ObjectNode prevAndNext(Integer id, Integer u_id) {
        json = mapper.createObjectNode();
        if (u_id == null) u_id = -1;
        Article prevA = articleService.prev(id, u_id);
        Article nextA = articleService.next(id, u_id);
        if (prevA != null && !prevA.getId().equals(id)) {
            json.put("prev_id", prevA.getId());
            json.put("prev_title", prevA.getTitle());
        }
        if (nextA != null && !nextA.getId().equals(id)) {
            json.put("next_id", nextA.getId());
            json.put("next_title", nextA.getTitle());
        }
        return json;
    }

    /**
     * 文章搜索
     */
    @RequestMapping(value = "/article/search")
    public String search(String keyword, Integer page, Integer size, Map<String, Object> map, HttpServletRequest request) {
        // 从cookie中获取登陆用户信息
        User user = userService.getCookieUser(request);
        // 日志记录
        logService.log(user, request);
        if (keyword != null && keyword.length() > 20) return "error";
        if (page == null) page = 1;
        if (size == null) size = BaseService.PAGESIZE;
        // 分页查询
        Page<Article> articlePage = articleService.findByKeyword(keyword, page, size);
        if (articlePage != null) {
            // 取出文章列表
            List<Article> articles = articleService.filter(articlePage.getContent());
            // 将数据返回到页面
            map.put("keyword", keyword.trim());
            map.put("articles", articles);
            map.put("size", articlePage.getPageable().getPageSize());
            map.put("page", page);
            map.put("totalPages", articlePage.getTotalPages());
            map.put("totalElements", articlePage.getTotalElements());
            // 判断上下页
            if (page + 1 <= articlePage.getTotalPages()) map.put("next_page", page + 1);
            if (page - 1 > 0) map.put("prev_page", page - 1);
            if (page - 1 > articlePage.getTotalPages()) map.put("prev_page", null);
        } else {
            map.put("articles", null);
        }
        return "search";
    }

    /**
     * 为文章移除密码
     */
    @ResponseBody
    @RequestMapping(value = "/article/removePassword", method = RequestMethod.GET)
    public ObjectNode removePassword(Integer id, HttpServletRequest request) {
        json = mapper.createObjectNode();
        // 从cookie中获取登陆用户信息
        User user = userService.getCookieUser(request);
        // 未登录
        if (user == null) {
            json.put(STATUS_NAME, STATUS_CODE_NOT_LOGIN);
            return json;
        }
        // 获取文章信息
        Article article = articleService.findById(id, 1);
        // 判断用户是否有权限操作
        if (userService.checkAuthority(user, article.getAuthor().getId(), 0)) {
            // 去除密码
            article.setPassword(null);
            try {
                // 将操作保存到数据库中
                articleService.save(article);
                json.put(STATUS_NAME, STATUS_CODE_SUCCESS);
            } catch (Exception e) {
                json.put(STATUS_NAME, STATUS_CODE_EXCEPTION);
            }
        }
        return json;
    }

    /**
     * 密码文章验证密码
     */
    @RequestMapping(value = "/article/checkPassword", method = RequestMethod.POST)
    public String checkPassword(Article article, Map<String, Object> map, HttpServletRequest request) {
        // 从cookie中获取登陆用户信息
        User user = userService.getCookieUser(request);
        // 判断密码是否为空字符串
        if (BaseService.checkNullStr(article.getPassword()))
            return "error";
        String password = BaseService.getHash(article.getPassword().trim(), "MD5"); // 加密密码
        // 获取文章信息
        Article a = articleService.findById(article.getId(), 1);
        // 文章不存在
        if (a == null) return "error";
        map.put("page", articleService.whereIsMyPage(a.getId(), a.getAuthor().getId(), 20));
        // 验证密码是否正确
        if (password != null && password.equals(a.getPassword())) {
            map.put("article", a);
            if (user != null) {
                if (a.getAuthor().getId().equals(user.getId()) || user.getAuthority() == 0) {
                    map.put("manage", 1);
                }
            }
            // 日志记录
            logService.log(user, request);
            return "article";
        } else {
            Article temp = new Article();
            temp.setId(a.getId());
            temp.setTitle("密码错误");
            temp.setAuthor(a.getAuthor());
            temp.setLastChangeTime(a.getLastChangeTime());
            temp.setTime(a.getTime());
            temp.setTop(a.getTop());
            map.put("article", temp);
            // 页面根据此字段判断是否该文章是否需要输入密码
            map.put("password", 1);
            map.put("manage", 0);
            // 日志记录
            logService.log(user, request);
            return "article";
        }
    }

    /**
     * 为文章设置密码
     */
    @ResponseBody
    @RequestMapping(value = "/article/addPassword", method = RequestMethod.POST)
    public ObjectNode addPassword(Integer id, String password, HttpServletRequest request) {
        json = mapper.createObjectNode();
        // 从cookie中获取登陆用户信息
        User user = userService.getCookieUser(request);
        // 未登录
        if (user == null) {
            json.put(STATUS_NAME, STATUS_CODE_NOT_LOGIN);
            return json;
        }
        // 密码为空
        if (BaseService.checkNullStr(password)) {
            json.put(STATUS_NAME, STATUS_CODE_NO_PERMISSION);
            return json;
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
                json.put(STATUS_NAME, STATUS_CODE_SUCCESS);
            } catch (Exception e) {
                json.put(STATUS_NAME, STATUS_CODE_EXCEPTION);
            }
        }
        return json;
    }

    /**
     * 从回收站恢复文章
     */
    @ResponseBody
    @RequestMapping(value = "/article/recover", method = RequestMethod.GET)
    public ObjectNode recover(Integer id, HttpServletRequest request) {
        json = mapper.createObjectNode();
        // 从cookie中获取登陆用户信息
        User user = userService.getCookieUser(request);
        // 未登录
        if (user == null) {
            json.put(STATUS_NAME, STATUS_CODE_NOT_LOGIN);
            return json;
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
                json.put(STATUS_NAME, STATUS_CODE_SUCCESS);
            } catch (Exception e) {
                json.put(STATUS_NAME, STATUS_CODE_EXCEPTION);
            }
        }
        // 日志记录
        logService.log(user, request);
        return json;
    }

    /**
     * 回收站
     */
    @RequestMapping(value = "/article/trash", method = RequestMethod.GET)
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
        // 日志记录
        logService.log(user, request);
        return "trash";
    }

    /**
     * 删除 / 隐藏文章
     */
    @ResponseBody
    @RequestMapping(value = "/article/delete", method = RequestMethod.GET)
    public ObjectNode delete(Integer id, Integer type, HttpServletRequest request) {
        json = mapper.createObjectNode();
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
                        json.put(STATUS_NAME, STATUS_CODE_SUCCESS);
                    } catch (Exception e) {
                        json.put(STATUS_NAME, STATUS_CODE_EXCEPTION);
                    }
                }
                break;
            // 真删除
            case 1:
            default:
                json.put(STATUS_NAME, STATUS_CODE_FILED);
        }
        // 日志记录
        logService.log(user, request);
        return json;
    }

    /**
     * 移除置顶 只有最高权限管理员可以做
     */
    @ResponseBody
    @RequestMapping(value = "/article/removeTop", method = RequestMethod.GET)
    public ObjectNode removeTop(HttpServletRequest request, Integer id) {
        json = mapper.createObjectNode();
        // 从cookie中获取登陆用户
        User user = userService.getCookieUser(request);
        // 查询文章信息
        Article article = articleService.findById(id, 1);
        // 判断权限
        if (user.getAuthority() > 0) {
            json.put(STATUS_NAME, STATUS_CODE_NO_PERMISSION);
        } else {
            // 移除置顶
            article.setTop(0);
            articleService.save(article);
            json.put(STATUS_NAME, STATUS_CODE_SUCCESS);
        }
        // 日志记录
        logService.log(user, request);
        return json;
    }

    /**
     * 设置置顶 只有最高权限管理员可以做
     */
    @ResponseBody
    @RequestMapping(value = "/article/setTop", method = RequestMethod.GET)
    public ObjectNode setTop(HttpServletRequest request, Integer id) {
        json = mapper.createObjectNode();
        // 从cookie中获取登陆用户
        User user = userService.getCookieUser(request);
        // 查询文章信息
        Article article = articleService.findById(id, 1);
        // 判断权限
        if (user.getAuthority() > 0)
            json.put(STATUS_NAME, STATUS_CODE_NO_PERMISSION);
        else {
            // 设置置顶
            if (articleService.setTop(article)) {
                json.put(STATUS_NAME, STATUS_CODE_SUCCESS);
            } else {
                json.put(STATUS_NAME, STATUS_CODE_FILED);
            }
        }
        // 日志记录
        logService.log(user, request);
        return json;
    }

    /**
     * 显示修改文章
     */
    @RequestMapping(value = "/article/updateOne", method = RequestMethod.GET)
    public String updateOne(HttpServletRequest request, Map<String, Object> map, Integer id) {
        // 从cookie中获取登陆用户
        User user = userService.getCookieUser(request);
        Article article;
        if (user != null) {
            // 普通用户无法修改隐藏文章
            if (user.getAuthority() == 0) {
                article = articleService.findById(id, 1);
            } else {
                article = articleService.findById(id, 0);
            }
            // 判断权限
            if (!userService.checkAuthority(user, article.getAuthor().getId(), 0)) {
                return "login";
            }
            map.put("article", article);
        } else {
            return "login";
        }
        // 日志记录
        logService.log(user, request);
        return "update";
    }

    /**
     * 发布文章
     */
    @ResponseBody
    @RequestMapping(value = "/article/post", method = RequestMethod.POST)
    public ObjectNode post(HttpServletRequest request, Article article) {
        json = mapper.createObjectNode();
        // 从cookie中获取登陆用户
        User user = userService.getCookieUser(request);
        // 检测重复操作
        if (article.getId() == null) {
            if (!logService.check(request, 5)) {
                json.put(STATUS_NAME, STATUS_CODE_TIMES_LIMIT);
                return json;
            }
        }
        // 日志记录
        logService.log(user, request);
        // 检测是否为空
        if (BaseService.checkNullStr(article.getTitle()) || BaseService.checkNullStr(article.getContent())) {
            json.put(STATUS_NAME, STATUS_CODE_FILED);
            return json;
        }
        try {
            // 修改文章
            if (article.getId() != null) {
                Article a;
                if (user.getAuthority() == 0) {
                    a = articleService.findById(article.getId(), 1);
                } else {
                    a = articleService.findById(article.getId(), 0);
                }
                a.setTitle(article.getTitle().trim());
                a.setContent(article.getContent().trim());
                a.setIntroduction(article.getIntroduction().trim());
                a.setLastChangeTime(new Date());
                articleService.save(a);
            } else {
                article.setTitle(BaseService.killHTML(article.getTitle().trim()));
                article.setContent(article.getContent().trim());
                article.setIntroduction(article.getIntroduction().trim());
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
            json.put(STATUS_NAME, STATUS_CODE_SUCCESS);
        } catch (Exception e) {
            json.put(STATUS_NAME, STATUS_CODE_EXCEPTION);
        }
        return json;
    }

    /**
     * 获取文章详情
     */
    @RequestMapping(value = "/article/{id}", method = RequestMethod.GET)
    public String one(HttpServletRequest request, Map<String, Object> map, @PathVariable("id") Integer id) {
        // 从cookie中获取登陆用户
        User user = userService.getCookieUser(request);
        // 日志记录
        logService.log(user, request);
        Article article;
        // 用户无法查看被隐藏的文章
        if (user != null && user.getAuthority() == 0)
            article = articleService.findById(id, 1);
        else
            article = articleService.findById(id, 0);
        if (article == null) return "error";
        if (article.getPassword() == null || article.getPassword().equals("")) {
            map.put("article", article);
            if (user != null)
                if (article.getAuthor().getId().equals(user.getId()) || user.getAuthority() == 0)
                    map.put("manage", 1);
        } else {
            map.put("article", article);
            map.put("password", 1);
            map.put("manage", 0);
        }
        map.put("page", articleService.whereIsMyPage(id, article.getAuthor().getId(), 20));
        return "article";
    }

    /**
     * 查询置顶文章 默认是三个
     */
    @JsonView({Article.Top.class})
    @ResponseBody
    @RequestMapping(value = "/article/top", method = RequestMethod.GET)
    public List top() {
        return articleService.top(2);
    }

    /**
     * 首页 json
     */
    @JsonView({Article.Index.class})
    @ResponseBody
    @RequestMapping(value = "/article/index", method = RequestMethod.GET)
    public List index(HttpServletRequest request) {
        json = mapper.createObjectNode();
        // 从 Cookie 中获取登陆信息
        User user = userService.getCookieUser(request);
        // 日志记录
        logService.log(user, request);
        Page<Article> articlePage = articleService.findAll(0, 10, 0);
        if (articlePage != null) {
            return articleService.filter(articlePage.getContent());
        } else
            return null;
    }

    /**
     * 获取文章列表
     */
    @RequestMapping(value = "/article/list")
    public String list(Map<String, Object> map, Integer page, Integer size, HttpServletRequest request) {
        // 从 Cookie 中获取登陆信息
        User user = userService.getCookieUser(request);
        // 日志记录
        logService.log(user, request);
        if (page == null) page = 1;
        if (size == null) size = 10;
        // 分页查询
        Page<Article> articlePage = articleService.findAll(page, size, 0);
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

    /**
     * 创建站点地图
     */
    @GetMapping("/site_map.xml")
    public void createSiteMapXml(HttpServletResponse response) throws IOException {
        response.setContentType(MediaType.APPLICATION_XML_VALUE);
        Writer writer = response.getWriter();
        writer.append(articleService.createSiteMapXmlContent());
    }

}
