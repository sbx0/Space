package cn.sbx0.space.dao;

import cn.sbx0.space.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

/**
 * 文章Dao
 */
public interface ArticleDao extends PagingAndSortingRepository<Article, Integer> {

    @Query(value = "SELECT * FROM articles a ORDER BY a.last_change_time DESC LIMIT 5", nativeQuery = true)
    List<Article> renew();

    /**
     * 查询某文章发布时间之后的文章条数
     *
     * @param id   文章ID
     * @param u_id 作者ID
     * @return 大于等于某ID的文章条数
     */
    @Query(value = "SELECT COUNT(*) FROM articles a WHERE a.top >= 0 AND a.id >= ?1 AND a.author_id = ?2", nativeQuery = true)
    Integer countLargeThanId(Integer id, Integer u_id);

    /**
     * 查询指定作者的上一条文章
     *
     * @param id   文章ID
     * @param u_id 用户ID
     * @return 该作者的上一篇文章
     */
    @Query(value = "SELECT * FROM articles a WHERE a.id < ?1 AND a.author_id = ?2 AND a.top >= 0 ORDER BY a.id DESC LIMIT 1", nativeQuery = true)
    Article prev(Integer id, Integer u_id);

    /**
     * 查询上一条文章
     *
     * @param id 文章ID
     * @return 该篇文章的上一篇文章
     */
    @Query(value = "SELECT * FROM articles a WHERE a.id < ?1 AND a.top >= 0 ORDER BY a.id DESC LIMIT 1", nativeQuery = true)
    Article prev(Integer id);

    /**
     * 查询下一条文章
     *
     * @param id 文章ID
     * @return 该篇文章的下一篇文章
     */
    @Query(value = "SELECT * FROM articles a WHERE a.id > ?1 AND a.top >= 0 ORDER BY a.id ASC LIMIT 1", nativeQuery = true)
    Article next(Integer id);

    /**
     * 查询指定作者的下一条文章
     *
     * @param id   文章ID
     * @param u_id 作者ID
     * @return 该作者的下一篇文章
     */
    @Query(value = "SELECT * FROM articles a WHERE a.id > ?1 AND a.author_id = ?2 AND a.top >= 0 ORDER BY a.id ASC LIMIT 1", nativeQuery = true)
    Article next(Integer id, Integer u_id);

    /**
     * 查询全部文章(包含隐藏文章)
     *
     * @param pageable 分页查询
     * @return 对应页数和条数的文章列表
     */
    @Query(value = "SELECT * FROM articles", nativeQuery = true)
    Page<Article> adminFindAll(Pageable pageable);


    /**
     * 按照ID查询某篇文章(包含隐藏文章)
     *
     * @param id 文章ID
     * @return 对应ID的文章
     */
    @Query(value = "SELECT * FROM articles a WHERE a.id = ?1", nativeQuery = true)
    Optional<Article> adminFindById(Integer id);

    /**
     * 站点地图查询全部文章(不包含隐藏文章)
     *
     * @return 文章列表
     */
    @Query(value = "SELECT * FROM articles a WHERE a.top >= 0 ORDER BY a.id DESC", nativeQuery = true)
    List<Article> findAll();

    /**
     * 查询未被隐藏的文章列表
     *
     * @param pageable 分页查询
     * @return 对应页数和条数的文章列表
     */
    @Query(value = "SELECT * FROM articles a WHERE a.top >= 0", nativeQuery = true)
    Page<Article> findAll(Pageable pageable);

    /**
     * 根据关键词查询文章
     *
     * @param keyword  关键词
     * @param pageable 分页查询
     * @return 对应页数和条数的文章列表
     */
    @Query(value = "SELECT * FROM articles a WHERE a.top >= 0 AND (UPPER(a.title) LIKE upper(?1) OR upper(a.introduction) LIKE upper(?1)) ORDER BY LENGTH(REPLACE(a.title,?1,''))", nativeQuery = true)
    Page<Article> findByKeyword(String keyword, Pageable pageable);

    /**
     * 根据ID查询未被隐藏的文章
     *
     * @param id 文章ID
     * @return 对应的文章
     */
    @Query(value = "SELECT * FROM articles a WHERE a.id = ?1 AND a.top >= 0", nativeQuery = true)
    Optional<Article> findById(Integer id);

    /**
     * 查询相应作者的文章
     *
     * @param id       作者ID
     * @param pageable 分页查询
     * @return 对应页数和条数的文章列表
     */
    @Query(value = "SELECT * FROM articles a WHERE a.author_id = ?1 AND a.top >= 0", nativeQuery = true)
    Page<Article> findByUser(Integer id, Pageable pageable);

    /**
     * 回收站
     * 查询被隐藏的文章
     *
     * @param pageable 分页查询
     * @return 对应页数和条数的文章列表
     */
    @Query(value = "SELECT * FROM articles a WHERE a.top < 0", nativeQuery = true)
    Page<Article> findTrash(Pageable pageable);

    /**
     * 回收站
     * 查询对应作者的被隐藏的文章
     *
     * @param id       作者ID
     * @param pageable 分页查询
     * @return 对应页数和条数的文章列表
     */
    @Query(value = "SELECT * FROM articles a WHERE a.top < 0 AND a.author_id = ?1", nativeQuery = true)
    Page<Article> findTrashByUser(Integer id, Pageable pageable);

    /**
     * 获取最大置顶
     *
     * @return 最大置顶数
     */
    @Query(value = "SELECT a.top FROM articles a WHERE a.top >= 0 ORDER BY a.top DESC LIMIT 1", nativeQuery = true)
    Integer getMaxTop();

    /**
     * 获取当前置顶文章数
     *
     * @return 置顶文章数
     */
    @Query(value = "SELECT count(*) FROM articles a WHERE a.top > 0", nativeQuery = true)
    Integer getTopNumber();

    /**
     * 查询置顶文章
     *
     * @param size 置顶文章条数
     * @return 相应的置顶文章列表
     */
    @Query(value = "SELECT * FROM articles a WHERE a.top > 0 ORDER BY a.top DESC,a.id DESC LIMIT ?1", nativeQuery = true)
    List<Article> top(Integer size);
}
