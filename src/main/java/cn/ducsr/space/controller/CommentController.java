package cn.ducsr.space.controller;

import cn.ducsr.space.entity.Comment;
import cn.ducsr.space.service.CommentService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 评论控制类
 */
@Controller
@RequestMapping("/comment")
public class CommentController extends BaseController {
    @Resource
    private CommentService commentService;

    /**
     * 测试
     */
    @ResponseBody
    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public void test() {
        for (int i = 0; i < 100; i++) {
            Comment comment = new Comment();
            comment.setContent("第" + i + "条评论！");
            comment.setUser_name("第" + i + "个测试用户！");
            comment.setTime(new Date());
            comment.setDislikes(i);
            comment.setLikes(i);
            comment.setEntity_id(i);
            comment.setEntity_type("article");
            comment.setFloor(i);
            comment.setTop(0);
            commentService.save(comment);
        }
    }

    /**
     * 查询评论
     *
     * @param type
     * @param id
     * @param page
     * @param size
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List list(String type, Integer id, Integer page, Integer size) {
        Page<Comment> commentPage = commentService.findByEntity(type, id, page - 1, size);
        if (commentPage != null) {
            List<Comment> comments = commentPage.getContent();
            return comments;
        } else {
            return null;
        }
    }
}
