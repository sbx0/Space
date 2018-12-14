package cn.sbx0.space.service;

import cn.sbx0.space.dao.CommentDao;
import cn.sbx0.space.entity.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService extends BaseService<Comment, Integer> {
    @Autowired
    private CommentDao commentDao;

    @Override
    public PagingAndSortingRepository<Comment, Integer> getDao() {
        return commentDao;
    }

    /**
     * 查询最新的limit条评论
     */
    public List<Comment> findNew(Integer limit) {
        if (limit == null) limit = 1;
        if (limit < 1) limit = 1;
        if (limit > 100) limit = 99;
        return commentDao.findNew(limit);
    }

    /**
     * 查询评论总条数
     */
    public Integer countCommentByEntity(String entity_type, Integer entity_id) {
        return commentDao.countCommentByEntity(entity_type, entity_id);
    }

    /**
     * 查询上一条评论的楼层
     */
    public Integer findPrevCommentFloor(String entity_type, Integer entity_id) {
        Integer floor = commentDao.findPrevCommentFloor(entity_type, entity_id);
        if (floor == null) floor = 1;
        return floor;
    }


    /**
     * 加载评论 根据实体
     */
    public Page<Comment> findByEntity(String entity_type, Integer entity_id, Integer page, Integer size) {
        Pageable pageable = buildPageable(page, size, buildSort("id", "DESC"));
        try {
            // 分页查询
            Page<Comment> commentPage = commentDao.findByEntity(entity_type, entity_id, pageable);
            if (commentPage.getContent().size() != 0)
                return commentPage;
            else
                return null;
        } catch (Exception e) {
            return null;
        }
    }
}
