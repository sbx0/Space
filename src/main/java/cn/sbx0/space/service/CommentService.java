package cn.sbx0.space.service;

import cn.sbx0.space.dao.CommentDao;
import cn.sbx0.space.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class CommentService extends BaseService {
    @Resource
    private CommentDao commentDao;

    public void save(Comment comment) {
        commentDao.save(comment);
    }

    /**
     * 查询最新的limit条评论
     *
     * @param limit 条数
     * @return 评论列表
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
        // 页数控制
        if (page > 9999) page = 9999;
        if (page < 0) page = 0;
        // 条数控制
        if (size > 1000) size = 1000;
        if (size < 1) size = 1;
        if (size == 0) size = 10;
        // 分页配置
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
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
