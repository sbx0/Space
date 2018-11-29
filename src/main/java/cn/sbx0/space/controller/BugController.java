package cn.sbx0.space.controller;

import cn.sbx0.space.entity.Bug;
import cn.sbx0.space.entity.User;
import cn.sbx0.space.service.BaseService;
import cn.sbx0.space.service.BugService;
import cn.sbx0.space.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/bug")
public class BugController extends BaseController {
    @Resource
    private BugService bugService;
    @Resource
    private UserService userService;
    private ObjectMapper mapper;
    private ObjectNode json;

    @Autowired
    public BugController(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * 解决反馈
     */
    @ResponseBody
    @GetMapping(value = "/solve")
    public ObjectNode solve(Integer id, HttpServletRequest request) {
        json = mapper.createObjectNode();
        // 从cookie中获取登陆用户信息
        User user = userService.getCookieUser(request);
        if (user == null) { // 未登录无法更新
            json.put(STATUS_NAME, STATUS_CODE_NOT_LOGIN);
            return json;
        } else if (user.getAuthority() > 0) { // 无权限
            json.put(STATUS_NAME, STATUS_CODE_NO_PERMISSION);
            return json;
        }
        Bug bug = bugService.findById(id);
        if (bug == null) {
            json.put(STATUS_NAME, STATUS_CODE_NOT_FOUND);
            return json;
        } else {
            bug.setSolvedTime(new Date());
            bug.setSolver(user);
            bug.setStatus(-1);
            try {
                bugService.save(bug);
                json.put(STATUS_NAME, STATUS_CODE_SUCCESS);
            } catch (Exception e) {
                json.put(STATUS_NAME, STATUS_CODE_EXCEPTION);
                return json;
            }
        }
        return json;
    }

    /**
     * 反馈详情
     */
    @ResponseBody
    @GetMapping(value = "/{id}")
    public Bug one(@PathVariable("id") Integer id) {
        Bug bug = bugService.findById(id);
        return bug;
    }

    /**
     * 获取未被隐藏的反馈列表
     */
    @ResponseBody
    @GetMapping("/solved")
    public ArrayNode solved(Integer page, Integer size, HttpServletRequest request) {
        // 从cookie中获取登陆用户信息
        User user = userService.getCookieUser(request);
        if (user == null) { // 未登录无法更新
            return null;
        } else if (user.getAuthority() > 0) { // 无权限
            return null;
        }
        if (page == null) page = 1;
        if (size == null) size = 50;
        Page<Bug> bugPage = bugService.findSolved(page - 1, size);
        List<Bug> bugList = bugPage.getContent();
        ArrayNode bugJsons = mapper.createArrayNode();
        for (Bug bug : bugList) {
            json = mapper.createObjectNode();
            json.put("id", bug.getId());
            json.put("name", bug.getName());
            json.put("grade", bug.getGrade());
            json.put("status", bug.getStatus());
            bugJsons.add(json);
        }
        return bugJsons;
    }

    /**
     * 获取未被隐藏的反馈列表
     */
    @ResponseBody
    @GetMapping("/list")
    public ArrayNode list(Integer page, Integer size, HttpServletRequest request) {
        // 从cookie中获取登陆用户信息
        User user = userService.getCookieUser(request);
        if (user == null) { // 未登录无法更新
            return null;
        } else if (user.getAuthority() > 0) { // 无权限
            return null;
        }
        if (page == null) page = 1;
        if (size == null) size = 50;
        Page<Bug> bugPage = bugService.findAll(page - 1, size);
        List<Bug> bugList = bugPage.getContent();
        ArrayNode bugJsons = mapper.createArrayNode();
        for (Bug bug : bugList) {
            json = mapper.createObjectNode();
            json.put("id", bug.getId());
            json.put("name", bug.getName());
            json.put("grade", bug.getGrade());
            json.put("status", bug.getStatus());
            bugJsons.add(json);
        }
        return bugJsons;
    }

    /**
     * 获取未被隐藏的反馈列表
     */
    @ResponseBody
    @GetMapping("/my")
    public ArrayNode my(Integer page, Integer size, HttpServletRequest request) {
        // 从cookie中获取登陆用户信息
        User user = userService.getCookieUser(request);
        if (user == null) {
            user = new User();
            user.setId(-1);
        }
        if (page == null) page = 1;
        if (size == null) size = 50;
        Page<Bug> bugPage = bugService.findMy(BaseService.getIpAddress(request), user.getId(), page - 1, size);
        List<Bug> bugList = bugPage.getContent();
        ArrayNode bugJsons = mapper.createArrayNode();
        for (Bug bug : bugList) {
            json = mapper.createObjectNode();
            json.put("id", bug.getId());
            json.put("name", bug.getName());
            json.put("grade", bug.getGrade());
            json.put("status", bug.getStatus());
            bugJsons.add(json);
        }
        return bugJsons;
    }

    /**
     * 发布反馈
     */
    @ResponseBody
    @PostMapping("/post")
    public ObjectNode post(Bug bug, HttpServletRequest request) {
        json = mapper.createObjectNode();
        try {
            bug.setId(null);
            bug.setSubmitTime(new Date());
            bug.setSolvedTime(null);
            bug.setStatus(0);
            bug.setIp(BaseService.getIpAddress(request));
            bug.setTop(0);
            if (bugService.save(bug))
                json.put(STATUS_NAME, STATUS_CODE_SUCCESS);
            else
                json.put(STATUS_NAME, STATUS_CODE_FILED);
        } catch (Exception e) {
            json.put(STATUS_NAME, STATUS_CODE_EXCEPTION);
        }
        return json;
    }

}
