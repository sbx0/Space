package cn.ducsr.space.service;

import cn.ducsr.space.dao.ArticleDao;
import cn.ducsr.space.entity.Article;
import cn.ducsr.space.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 文章服务层
 */
@Service
public class ArticleService extends BaseService {
    @Resource
    private ArticleDao articleDao;

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
        // 分页配置
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        if (id < 1) return null;
        return articleDao.findByUser(id, pageable);
    }

    /**
     * 查询一篇博文
     *
     * @param id 博文ID
     * @return 博文
     */
    public Article findById(Integer id) {
        try {
            return articleDao.findById(id).get();
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
    public Page<Article> findAll(Integer page, Integer size) {
        // 页数控制
        if (page > 9999) page = 9999;
        if (page < 0) page = 0;
        // 条数控制
        if (size > 1000) size = 1000;
        if (size < 1) size = 1;
        // 分页配置
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        try {
            // 分页查询
            return articleDao.findAll(pageable);
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
        simpleArticle.setContent(article.getContent());
        simpleArticle.setTitle(article.getTitle());
        simpleArticle.setTime(article.getTime());
        simpleArticle.setLastChangeTime(article.getLastChangeTime());

        simpleUser.setId(article.getAuthor().getId());
        simpleUser.setName(article.getAuthor().getName());
        simpleUser.setSignature(article.getAuthor().getSignature());
        simpleArticle.setAuthor(simpleUser);

        return simpleArticle;
    }

}
