package cn.ducsr.space.dao;

import cn.ducsr.space.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * 文章Dao
 */
public interface ArticleDao extends PagingAndSortingRepository<Article, Integer> {

    /**
     * 获取最大置顶
     *
     * @return
     */
    @Query(value = "select top from articles a order by a.top desc limit 1", nativeQuery = true)
    Integer getMaxTop();

    /**
     * 查询用户文章
     *
     * @param id
     * @return
     */
    @Query(value = "select  * from articles a where a.author_id = ?1", nativeQuery = true)
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
