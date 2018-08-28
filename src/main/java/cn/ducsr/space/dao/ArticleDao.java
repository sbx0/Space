package cn.ducsr.space.dao;

import cn.ducsr.space.entity.Article;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * 文章Dao
 */
public interface ArticleDao extends PagingAndSortingRepository<Article, Integer> {

    @Query(value = "select * from articles a order by a.top desc, a.id desc limit ?1", nativeQuery = true)
    List<Article> top(Integer size);
}
