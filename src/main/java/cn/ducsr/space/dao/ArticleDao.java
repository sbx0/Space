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
	 * 查询指定用户的上一条博文
	 *
	 * @param id
	 * @param u_id
	 * @return
	 */
	@Query(value = "select * from articles a where a.id < ?1 and a.author_id = ?2 and a.top >= 0 order by a.id desc limit 1", nativeQuery = true)
	Article prev(int id, int u_id);


	/**
	 * 查询上一条博文
	 *
	 * @param id
	 * @return
	 */
	@Query(value = "select * from articles a where a.id < ?1 and a.top >= 0 order by a.id desc limit 1", nativeQuery = true)
	Article prev(int id);


	/**
	 * 查询下一条博文
	 *
	 * @param id
	 * @return
	 */
	@Query(value = "select * from articles a where a.id > ?1 and a.top >= 0 order by a.id asc limit 1", nativeQuery = true)
	Article next(int id);


	/**
	 * 查询指定用户的下一条博文
	 *
	 * @param id
	 * @param u_id
	 * @return
	 */
	@Query(value = "select * from articles a where a.id > ?1 and a.author_id = ?2 and a.top >= 0 order by a.id asc limit 1", nativeQuery = true)
	Article next(int id, int u_id);


	/**
	 * 查询文章列表
	 */
	@Query(value = "select  * from articles", nativeQuery = true)
	Page<Article> adminFindAll(Pageable pageable);


	/**
	 * 查询文章
	 */
	@Query(value = "select  * from articles a where a.id = ?1", nativeQuery = true)
	Optional<Article> adminFindById(Integer id);

	/**
	 * 查询所有文章
	 */
	@Query(value = "select  * from articles a where a.top >= 0 order by a.time desc,a.last_change_time desc", nativeQuery = true)
	List<Article> findAll();


	/**
	 * 查询未被隐藏的文章列表
	 */
	@Query(value = "select  * from articles a where a.top >= 0", nativeQuery = true)
	Page<Article> findAll(Pageable pageable);

	/**
	 * 查询未被隐藏的文章
	 */
	@Query(value = "select  * from articles a where a.top >= 0 and (a.title like ?1 or a.content like ?1)", nativeQuery = true)
	Page<Article> findByKeyword(String keyword, Pageable pageable);


	/**
	 * 查询未被隐藏的文章
	 */
	@Query(value = "select  * from articles a where a.top >= 0 and a.id = ?1", nativeQuery = true)
	Optional<Article> findById(Integer id);


	/**
	 * 查询用户文章
	 *
	 * @param id
	 * @return
	 */
	@Query(value = "select  * from articles a where a.author_id = ?1 and a.top >= 0", nativeQuery = true)
	Page<Article> findByUser(Integer id, Pageable pageable);


	/**
	 * 查询被隐藏的文章列表
	 */
	@Query(value = "select  * from articles a where a.top < 0", nativeQuery = true)
	Page<Article> findTrash(Pageable pageable);


	/**
	 * 查询被隐藏的文章列表 根据用户
	 */
	@Query(value = "select  * from articles a where a.top < 0 and a.author_id = ?1", nativeQuery = true)
	Page<Article> findTrashByUser(Integer id, Pageable pageable);


	/**
	 * 获取最大置顶
	 *
	 * @return
	 */
	@Query(value = "select top from articles a where a.top >= 0 order by a.top desc limit 1", nativeQuery = true)
	Integer getMaxTop();


	/**
	 * 获取当前置顶文章数
	 *
	 * @return
	 */
	@Query(value = "select  count(*) from articles a where a.top > 0", nativeQuery = true)
	Integer getTopNumber();


	/**
	 * 查询置顶文章
	 *
	 * @param size
	 * @return
	 */
	@Query(value = "select * from articles a where a.top > 0 order by a.top desc, a.id desc limit ?1", nativeQuery = true)
	List<Article> top(Integer size);
}
