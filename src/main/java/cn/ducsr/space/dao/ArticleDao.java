package cn.ducsr.space.dao;

import cn.ducsr.space.entity.Article;
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

    /**
     * 查询文章
     */
    @Query(value = "select  * from articles a where a.id = ?1", nativeQuery = true)
    Optional<Article> adminFindById(Integer id);

    /**
     * 查询文章列表
     */
    @Query(value = "select  * from articles", nativeQuery = true)
    Page<Article> adminFindAll(Pageable pageable);

    /**
     * 查询未被隐藏的文章
     */
    @Query(value = "select  * from articles a where a.top >= 0 and a.id = ?1", nativeQuery = true)
    Optional<Article> findById(Integer id);

    /**
     * 查询未被隐藏的文章列表
     */
    @Query(value = "select  * from articles a where a.top >= 0", nativeQuery = true)
    Page<Article> findAll(Pageable pageable);

    /**
     * 获取最大置顶
     *
     * @return
     */
    @Query(value = "select top from articles a where a.top >= 0 order by a.top desc limit 1", nativeQuery = true)
    Integer getMaxTop();

    /**
     * 查询用户文章
     *
     * @param id
     * @return
     */
    @Query(value = "select  * from articles a where a.author_id = ?1 and a.top >= 0", nativeQuery = true)
    Page<Article> findByUser(Integer id, Pageable pageable);

    /**
     * 查询置顶文章
     *
     * @param size
     * @return
     */
    @Query(value = "select * from articles a where a.top > 0 order by a.top desc, a.id desc limit ?1", nativeQuery = true)
    List<Article> top(Integer size);
}
