package cn.sbx0.space.service;

import cn.sbx0.space.dao.ArticleDao;
import cn.sbx0.space.dao.UserDao;
import cn.sbx0.space.entity.Article;
import cn.sbx0.space.entity.User;
import com.redfin.sitemapgenerator.ChangeFreq;
import com.redfin.sitemapgenerator.WebSitemapGenerator;
import com.redfin.sitemapgenerator.WebSitemapUrl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 文章服务层
 */
@Service
public class ArticleService extends BaseService {
    @Resource
    private ArticleDao articleDao;
    @Resource
    private UserDao userDao;
    @Value("${sbx0.SITE_MAP.DOMAIN}")
    private String SITE_MAP_DOMAIN;

    /**
     * 查询某文章在第几页
     *
     * @param id
     * @param size
     * @return
     */
    public Integer whereIsMyPage(Integer id, Integer u_id, Integer size) {
        int me = articleDao.countLargeThanId(id, u_id);
        int page = me % size == 0 ? me / size : me / size + 1;
        return page;
    }

    /**
     * 查询上一条博文
     *
     * @param id
     * @param u_id
     * @return
     */
    public Article prev(int id, int u_id) {
        if (u_id > 0)
            return articleDao.prev(id, u_id);
        else
            return articleDao.prev(id);
    }

    /**
     * 查询下一条博文
     *
     * @param id
     * @param u_id
     * @return
     */
    public Article next(int id, int u_id) {
        if (u_id > 0)
            return articleDao.next(id, u_id);
        else
            return articleDao.next(id);
    }

    /**
     * 根据关键词查询
     *
     * @param keyword
     * @param page
     * @param size
     * @return
     */
    public Page<Article> findByKeyword(String keyword, Integer page, Integer size) {
        if (BaseService.checkNullStr(keyword))
            return null;
        keyword = "%" + keyword + "%";
        // 页数控制
        if (page > 9999) page = 9999;
        if (page < 0) page = 0;
        // 条数控制
        if (size > 1000) size = 1000;
        if (size < 1) size = 1;
        if (size == 0) size = 10;
        // 分页配置
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        try {
            // 分页查询
            Page<Article> articlePage = articleDao.findByKeyword(keyword, pageable);
            return articlePage;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 查询被隐藏的文章列表 根据用户
     *
     * @return 文章列表
     */
    public Page<Article> findTrashByUser(Integer id, Integer page, Integer size) {
        // 页数控制
        if (page > 9999) page = 9999;
        if (page < 0) page = 0;
        // 条数控制
        if (size > 1000) size = 1000;
        if (size < 1) size = 1;
        if (size == 0) size = 10;
        // 分页配置
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        try {
            // 分页查询
            Page<Article> articlePage = articleDao.findTrashByUser(id, pageable);
            if (articlePage.getContent().size() != 0)
                return articleDao.findTrashByUser(id, pageable);
            else
                return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 查询被隐藏的文章列表
     *
     * @return 文章列表
     */
    public Page<Article> findTrash(Integer page, Integer size) {
        // 页数控制
        if (page > 9999) page = 9999;
        if (page < 0) page = 0;
        // 条数控制
        if (size > 1000) size = 1000;
        if (size < 1) size = 1;
        if (size == 0) size = 10;
        // 分页配置
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        try {
            // 分页查询
            return articleDao.findTrash(pageable);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 设置置顶
     *
     * @param article
     * @return
     */
    public boolean setTop(Article article) {
        if (articleDao.getTopNumber() > 2) return false;
        int max = getMaxTop();
        article.setTop(max + 1);
        try {
            save(article);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取最大置顶
     *
     * @return
     */
    public Integer getMaxTop() {
        int max = articleDao.getMaxTop();
        if (max < 0) max = 0;
        return max;
    }

    /**
     * 用户文章列表
     *
     * @param id
     * @param page
     * @param size
     * @return
     */
    public Page<Article> findByUser(Integer id, Integer page, Integer size) {
        // 页数控制
        if (page > 9999) page = 9999;
        if (page < 0) page = 0;
        // 条数控制
        if (size > 1000) size = 1000;
        if (size < 1) size = 1;
        if (size == 0) size = 10;
        // 分页配置
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        // 分页查询
        Page<Article> articlePage = articleDao.findByUser(id, pageable);
        if (id < 1) return null;
        if (articlePage.getContent().size() != 0)
            return articleDao.findByUser(id, pageable);
        else
            return null;
    }

    /**
     * 查询一篇博文
     *
     * @param id 博文ID
     * @return 博文
     */
    public Article findById(Integer id, Integer type) {
        try {
            if (type == 0)
                return articleDao.findById(id).get();
            else
                return articleDao.adminFindById(id).get();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 查询置顶文章
     *
     * @return
     */
    public List<Article> top(Integer size) {
        if (size > 1000) size = 1000;
        if (size < 1) size = 1;
        return filter(articleDao.top(size));
    }

    /**
     * 查询文章列表
     *
     * @return 文章列表
     */
    public Page<Article> findAll(Integer page, Integer size, Integer type) {
        // 页数控制
        if (page > 9999) page = 9999;
        if (page < 0) page = 0;
        // 条数控制
        if (size > 1000) size = 1000;
        if (size < 1) size = 1;
        if (size == 0) size = 10;
        // 分页配置
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        try {
            // 分页查询
            Page<Article> articlePage;
            if (type == 0)
                articlePage = articleDao.findAll(pageable);
            else
                articlePage = articleDao.adminFindAll(pageable);
            if (articlePage.getContent().size() > 0)
                return articlePage;
            else
                return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 保存文章
     *
     * @param article 文章
     * @return 操作成功与否
     */
    @Transactional
    public boolean save(Article article) {
        try {
            articleDao.save(article);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 公用方法

    /**
     * 过滤掉不想让用户看见的信息
     *
     * @param articleList 文章列表
     * @return 文章列表
     */
    public List<Article> filter(List<Article> articleList) {
        List<Article> articles = new ArrayList();
        for (int i = 0; i < articleList.size(); i++) {
            Article article = filter(articleList.get(i));
            if (article.getPassword() == null)
                articles.add(article);
        }
        return articles;
    }

    /**
     * 过滤掉不想让用户看见的信息
     *
     * @param article 文章
     * @return 文章
     */
    public Article filter(Article article) {

        Article simpleArticle = new Article();
        User simpleUser = new User();

        simpleArticle.setId(article.getId());
        simpleArticle.setTop(article.getTop());
        simpleArticle.setDislikes(article.getDislikes());
        simpleArticle.setLikes(article.getLikes());
        simpleArticle.setComments(article.getComments());
        simpleArticle.setViews(article.getViews());
        simpleArticle.setTitle(article.getTitle());

        if (article.getPassword() == null) {
            if (checkNullStr(article.getIntroduction())) {
                String content = article.getContent().trim();
                if (content.length() > 250) {
                    String killHTMLString = BaseService.killHTML(content);
                    if (killHTMLString.length() > 250)
                        simpleArticle.setContent(killHTMLString.substring(0, 250) + " ...");
                    else simpleArticle.setContent(killHTMLString + " ...");
                } else simpleArticle.setContent(BaseService.killHTML(content) + " ...");
            } else simpleArticle.setContent(BaseService.killHTML(article.getIntroduction()));
        } else simpleArticle.setContent("请输入密码后查看");

        simpleArticle.setTime(article.getTime());
        simpleArticle.setLastChangeTime(article.getLastChangeTime());

        simpleUser.setId(article.getAuthor().getId());
        simpleUser.setName(article.getAuthor().getName());
        simpleUser.setSignature(article.getAuthor().getSignature());
        simpleArticle.setAuthor(simpleUser);

        return simpleArticle;
    }


    /**
     * 创建站点地图
     *
     * @return
     */
    public String createSiteMapXmlContent() {
        String baseUrl = "http://" + SITE_MAP_DOMAIN;
        WebSitemapGenerator wsg = null;
        try {
            wsg = new WebSitemapGenerator(baseUrl);
            wsg.addUrl(createWebSitemapUrl(baseUrl + "/index.html", 1.0, new Date())); // 首页
            wsg.addUrl(createWebSitemapUrl(baseUrl + "/article/search", 0.9, new Date())); // 搜索
            wsg.addUrl(createWebSitemapUrl(baseUrl + "/article/list", 0.9, new Date())); // 发布文章
            // 添加文章详情页
            List<Article> articles = articleDao.findAll();
            for (Article article : articles) {
                if (article.getLastChangeTime() != null)
                    wsg.addUrl(createWebSitemapUrl(baseUrl + "/article/" + article.getId(), 0.9, article.getLastChangeTime())); // 文章
                else
                    wsg.addUrl(createWebSitemapUrl(baseUrl + "/article/" + article.getId(), 0.9, article.getTime())); // 文章
            }
            // 添加用户详情页
            List<User> users = userDao.findAll();
            for (User user : users) {
                wsg.addUrl(createWebSitemapUrl(baseUrl + "/user/" + user.getId(), 0.9, new Date())); // 用户
            }
            wsg.addUrl(createWebSitemapUrl(baseUrl + "/log/list", 0.1, new Date())); // 日志列表
            wsg.addUrl(createWebSitemapUrl(baseUrl + "/article/trash", 0.1, new Date())); // 回收站
            wsg.addUrl(createWebSitemapUrl(baseUrl + "/login.html", 0.1, new Date())); // 登陆 / 注册
            wsg.addUrl(createWebSitemapUrl(baseUrl + "/markdown.html", 0.1, new Date())); // 发布文章
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return String.join("", wsg.writeAsStrings());
    }

    /**
     * 拼接 WebSitemapUrl
     *
     * @param url
     * @param priority
     * @param date
     * @return
     */
    public WebSitemapUrl createWebSitemapUrl(String url, Double priority, Date date) {
        try {
            WebSitemapUrl webSitemapUrl = new WebSitemapUrl.Options(url)
                    .lastMod(date)
                    .priority(priority)
                    .changeFreq(ChangeFreq.DAILY)
                    .build();
            return webSitemapUrl;
        } catch (Exception e) {
            return null;
        }
    }

}