package cn.ducsr.space.controller;

import cn.ducsr.space.entity.Comment;
import cn.ducsr.space.entity.User;
import cn.ducsr.space.service.BaseService;
import cn.ducsr.space.service.CommentService;
import cn.ducsr.space.service.UserService;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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
    @Resource
    private UserService userService;

    /**
     * 发布评论
     *
     * @param comment
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/post", method = RequestMethod.POST)
    public ObjectNode post(Comment comment, HttpServletRequest request) {
        if (BaseService.checkNullStr(comment.getContent())) {
            objectNode.put("status", 1);
        } else {
            // 从cookie中获取登陆用户信息
            User user = userService.getCookieUser(request);
            // 若登录
            if (user != null) {
                comment.setUser_id(user.getId());
                comment.setUser_name(user.getName());
            } else if (BaseService.checkNullStr(comment.getUser_name())) {
                comment.setUser_name("匿名");
            }
//            try {
                comment.setTime(new Date());
                comment.setDislikes(0);
                comment.setLikes(0);
                comment.setTop(0);
                comment.setUser_ip("127.0.0.1");
                Integer count = commentService.countCommentByEntity(comment.getEntity_type(), comment.getEntity_id());
                Integer floor;
                if (count > 0)
                    floor = commentService.findPrevCommentFloor(comment.getEntity_type(), comment.getEntity_id());
                else
                    floor = 0;
                comment.setFloor(floor + 1);
                commentService.save(comment);
                objectNode.put("status", 0);
//            } catch (Exception e) {
//                objectNode.put("status", 1);
//            }
        }
        return objectNode;
    }

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
