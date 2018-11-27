package cn.sbx0.space.controller;

import cn.sbx0.space.entity.Article;
import cn.sbx0.space.entity.Comment;
import cn.sbx0.space.entity.User;
import cn.sbx0.space.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
    @Resource
    private LogService logService;
    @Resource
    private ArticleService articleService;
    private ObjectMapper mapper;
    private ObjectNode json;

    @Autowired
    public CommentController(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * 最新评论
     */
    @ResponseBody
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public ArrayNode index(HttpServletRequest request) {
        // 从cookie中获取登陆用户信息
        User user = userService.getCookieUser(request);
        // 日志记录
        logService.log(user, request);
        List<Comment> comments = commentService.findNew(10);
        ArrayNode arrayNode = mapper.createArrayNode();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        for (int i = 0; i < comments.size(); i++) {
            Comment comment = comments.get(i);
            json = mapper.createObjectNode();
            json.put("entity_id", comment.getEntity_id());
            json.put("entity_type", comment.getEntity_type());
            json.put("content", comment.getContent());
            json.put("user_ip", comment.getUser_ip());
            json.put("user_name", comment.getUser_name());
            json.put("time", dateFormat.format(comment.getTime()));
            json.put("id",comment.getId());
            arrayNode.add(json);
        }
        return arrayNode;
    }

    /**
     * 发布评论
     */
    @ResponseBody
    @RequestMapping(value = "/post", method = RequestMethod.POST)
    public ObjectNode post(Comment comment, HttpServletRequest request) {
        json = mapper.createObjectNode();
        // 从cookie中获取登陆用户信息
        User user = userService.getCookieUser(request);
        // 检测重复操作
        if (!logService.check(request, 5)) {
            json.put(STATUS_NAME, STATUS_CODE_TIMES_LIMIT);
        } else if (BaseService.checkNullStr(comment.getContent())) {
            json.put(STATUS_NAME, STATUS_CODE_NOT_FOUND);
        } else {
            // 若登录
            if (user != null) {
                comment.setUser_id(user.getId());
                comment.setUser_name(BaseService.killHTML(user.getName()));
            } else if (BaseService.checkNullStr(comment.getUser_name())) {
                comment.setUser_name("匿名");
            }
            try {
                comment.setContent(BaseService.killHTML(comment.getContent()));
                comment.setTime(new Date());
                comment.setDislikes(0);
                comment.setLikes(0);
                comment.setTop(0);
                // 获取用户IP
                comment.setUser_ip(BaseService.getIpAddress(request));
                Integer count = commentService.countCommentByEntity(comment.getEntity_type(), comment.getEntity_id());
                Integer floor;
                if (count > 0)
                    floor = commentService.findPrevCommentFloor(comment.getEntity_type(), comment.getEntity_id());
                else
                    floor = 0;
                comment.setFloor(floor + 1);
                commentService.save(comment);
                Article article = articleService.findById(comment.getEntity_id(), 1);
                article.setComments(article.getComments() + 1);
                json.put(STATUS_NAME, STATUS_CODE_SUCCESS);
            } catch (Exception e) {
                json.put(STATUS_NAME, STATUS_CODE_EXCEPTION);
            }
        }
        // 日志记录
        logService.log(user, request);
        return json;
    }

    /**
     * 查询评论
     */
    @ResponseBody
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List list(String type, Integer id, Integer page, Integer size) {
        Page<Comment> commentPage = commentService.findByEntity(type, id, page - 1, size);
        if (commentPage != null) {
            return commentPage.getContent();
        } else {
            return null;
        }
    }
}
