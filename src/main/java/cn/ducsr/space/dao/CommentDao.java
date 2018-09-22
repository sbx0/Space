package cn.ducsr.space.dao;

import cn.ducsr.space.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CommentDao extends PagingAndSortingRepository<Comment, Integer> {


    /**
     * 查询某实体类评论总条数
     *
     * @param entity_type
     * @param entity_id
     * @return
     */
    @Query(value = "select count(*) from comments c where c.top >= 0 and c.entity_type = ?1 and c.entity_id = ?2", nativeQuery = true)
    Integer countCommentByEntity(String entity_type, Integer entity_id);


    /**
     * 查询上一条评论的楼层
     *
     * @param entity_type
     * @param entity_id
     * @return
     */
    @Query(value = "select floor from comments c where c.top >= 0 and c.entity_type = ?1 and c.entity_id = ?2 order by c.id desc limit 1", nativeQuery = true)
    Integer findPrevCommentFloor(String entity_type, Integer entity_id);

    /**
     * 按种类加载评论
     *
     * @param entity_type 实体类种类
     * @param entity_id   实体类ID
     * @return
     */
    @Query(value = "select  * from comments c where c.top >= 0 and c.entity_type = ?1 and c.entity_id = ?2", nativeQuery = true)
    Page<Comment> findByEntity(String entity_type, Integer entity_id, Pageable pageable);

    // 删除评论 deleteById
    // 删除评论 deleteByTypeId
    // 查询评论
}
