package cn.sbx0.space.service;

import cn.sbx0.space.dao.ArticleDao;
import cn.sbx0.space.dao.UserDao;
import cn.sbx0.space.entity.Article;
import cn.sbx0.space.entity.User;
import com.redfin.sitemapgenerator.ChangeFreq;
import com.redfin.sitemapgenerator.WebSitemapGenerator;
import com.redfin.sitemapgenerator.WebSitemapUrl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 文章服务层
 */
@Service
public class ArticleService extends BaseService<Article, Integer> {
    @Resource
    private ArticleDao articleDao;

    @Override
    public PagingAndSortingRepository<Article, Integer> getDao() {
        return articleDao;
    }

    @Resource
    private UserDao userDao;
    @Value("${sbx0.SITE_MAP.DOMAIN}")
    private String SITE_MAP_DOMAIN;

    public List<Article> renew() {
        return articleDao.renew();
    }

    /**
     * 文章排行
     *
     * @param days 统计的天数
     * @param size 返回的条数
     */
    public List<Article> rank(Integer days, Integer size) {
        return null;
    }

    /**
     * 查询某文章在第几页
     */
    public Integer whereIsMyPage(Integer id, Integer u_id, Integer size) {
        int me = articleDao.countLargeThanId(id, u_id);
        return me % size == 0 ? me / size : me / size + 1;
    }

    /**
     * 查询上一条博文
     */
    public Article prev(int id, int u_id) {
        if (u_id > 0)
            return articleDao.prev(id, u_id);
        else
            return articleDao.prev(id);
    }

    /**
     * 查询下一条博文
     */
    public Article next(int id, int u_id) {
        if (u_id > 0)
            return articleDao.next(id, u_id);
        else
            return articleDao.next(id);
    }

    /**
     * 根据关键词查询
     */
    public Page<Article> findByKeyword(String keyword, Integer page, Integer size) {
        if (keyword == null) return null;
        else keyword = "%" + keyword + "%";
        if (BaseService.checkNullStr(keyword))
            return null;
        try {
            // 分页查询
            return articleDao.findByKeyword(keyword, BaseService.buildPageable(page, size, buildSort("id", "DESC")));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 查询被隐藏的文章列表 根据用户
     */
    public Page<Article> findTrashByUser(Integer id, Integer page, Integer size) {
        Pageable pageable = buildPageable(page, size, buildSort("id", "DESC"));
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
     */
    public Page<Article> findTrash(Integer page, Integer size) {
        Pageable pageable = buildPageable(page, size, buildSort("id", "DESC"));
        try {
            // 分页查询
            return articleDao.findTrash(pageable);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 设置置顶
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
     */
    private Integer getMaxTop() {
        int max = articleDao.getMaxTop();
        if (max < 0) max = 0;
        return max;
    }

    /**
     * 用户文章列表
     */
    public Page<Article> findByUser(Integer id, Integer page, Integer size) {
        Pageable pageable = buildPageable(page, size, buildSort("id", "DESC"));
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
     */
    public ArrayList<Article> top(Integer size) {
        if (size > 1000) size = 1000;
        if (size < 1) size = 1;
        return filter(articleDao.top(size));
    }

    /**
     * 查询文章列表
     */
    public Page<Article> findAll(Integer page, Integer size, Integer type) {
        Pageable pageable = buildPageable(page, size, buildSort("id", "DESC"));
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

    // 公用方法

    /**
     * 过滤掉不想让用户看见的信息
     */
    public ArrayList<Article> filter(List<Article> articleList) {
        ArrayList<Article> articles = new ArrayList<>();
        for (Article anArticleList : articleList) {
            Article article = filter(anArticleList);
            if (article.getPassword() == null) {
                articles.add(article);
            }
        }
        return articles;
    }

    /**
     * 过滤掉不想让用户看见的信息
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
     */
    public String createSiteMapXmlContent() {
        String baseUrl = "http://" + SITE_MAP_DOMAIN;
        WebSitemapGenerator wsg = null;
        try {
            wsg = new WebSitemapGenerator(baseUrl);
            wsg.addUrl(createWebSiteMapUrl(baseUrl + "/index.html", 1.0, new Date())); // 首页
            wsg.addUrl(createWebSiteMapUrl(baseUrl + "/article/search", 0.9, new Date())); // 搜索
            wsg.addUrl(createWebSiteMapUrl(baseUrl + "/article/list", 0.9, new Date())); // 发布文章
            // 添加文章详情页
            List<Article> articles = articleDao.findAll();
            for (Article article : articles) {
                if (article.getLastChangeTime() != null)
                    wsg.addUrl(createWebSiteMapUrl(baseUrl + "/article/" + article.getId(), 0.9, article.getLastChangeTime())); // 文章
                else
                    wsg.addUrl(createWebSiteMapUrl(baseUrl + "/article/" + article.getId(), 0.9, article.getTime())); // 文章
            }
            // 添加用户详情页
            List<User> users = userDao.findAll();
            for (User user : users) {
                wsg.addUrl(createWebSiteMapUrl(baseUrl + "/user/" + user.getId(), 0.9, new Date())); // 用户
            }
            wsg.addUrl(createWebSiteMapUrl(baseUrl + "/log/list", 0.1, new Date())); // 日志列表
            wsg.addUrl(createWebSiteMapUrl(baseUrl + "/article/trash", 0.1, new Date())); // 回收站
            wsg.addUrl(createWebSiteMapUrl(baseUrl + "/login.html", 0.1, new Date())); // 登陆 / 注册
            wsg.addUrl(createWebSiteMapUrl(baseUrl + "/markdown.html", 0.1, new Date())); // 发布文章
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        assert wsg != null;
        return String.join("", wsg.writeAsStrings());
    }

    /**
     * 拼接 WebSiteMapUrl
     */
    public WebSitemapUrl createWebSiteMapUrl(String url, Double priority, Date date) {
        try {
            return new WebSitemapUrl.Options(url)
                    .lastMod(date)
                    .priority(priority)
                    .changeFreq(ChangeFreq.DAILY)
                    .build();
        } catch (Exception e) {
            return null;
        }
    }

}