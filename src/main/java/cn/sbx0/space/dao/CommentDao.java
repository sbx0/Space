package cn.sbx0.space.dao;

import cn.sbx0.space.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * 评论Dao
 */
public interface CommentDao extends PagingAndSortingRepository<Comment, Integer> {

    /**
     * 查询某实体类评论总条数
     *
     * @param entity_type 实体类别
     * @param entity_id   实体ID
     * @return 条数
     */
    @Query(value = "SELECT count(*) FROM comments c WHERE c.top >= 0 AND c.entity_type = ?1 AND c.entity_id = ?2", nativeQuery = true)
    Integer countCommentByEntity(String entity_type, Integer entity_id);

    /**
     * 查询上一条评论的楼层
     *
     * @param entity_type 实体类别
     * @param entity_id   实体ID
     * @return 上一条评论的楼层
     */
    @Query(value = "SELECT c.floor FROM comments c WHERE c.top >= 0 AND c.entity_type = ?1 AND c.entity_id = ?2 ORDER BY c.id DESC limit 1", nativeQuery = true)
    Integer findPrevCommentFloor(String entity_type, Integer entity_id);

    /**
     * 按种类加载评论
     *
     * @param entity_type 实体类种类
     * @param entity_id   实体ID
     * @param pageable    分页查询
     * @return 对应页数和条数的评论列表
     */
    @Query(value = "SELECT * FROM comments c WHERE c.top >= 0 AND c.entity_type = ?1 AND c.entity_id = ?2", nativeQuery = true)
    Page<Comment> findByEntity(String entity_type, Integer entity_id, Pageable pageable);

    // 删除评论 deleteById
    // 删除评论 deleteByTypeId
    // 查询评论
}
