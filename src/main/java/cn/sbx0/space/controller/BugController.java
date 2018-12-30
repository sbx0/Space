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
public class BugController extends BaseController<Bug, Integer> {
    @Autowired
    private BugService bugService;
    @Resource
    private UserService userService;

    @Override
    public BaseService<Bug, Integer> getService() {
        return bugService;
    }

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
        if (bug == null) { // 不存在
            json.put(STATUS_NAME, STATUS_CODE_NOT_FOUND);
            return json;
        } else if (bug.getStatus() == -1) { // 已被解决
            json.put(STATUS_NAME, STATUS_CODE_REPEAT);
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
     * 获取的反馈列表
     */
    @ResponseBody
    @GetMapping("/list")
    public ArrayNode list(Integer page, Integer size, String type, HttpServletRequest request) {
        // 从cookie中获取登陆用户信息
        User user = userService.getCookieUser(request);
        if (page == null) page = 1;
        if (size == null) size = 20;
        Page<Bug> bugPage = null;
        ArrayNode bugJsons = mapper.createArrayNode();
        if (BaseService.checkNullStr(type)) { // 尚未被解决的
            bugPage = bugService.findAll(page - 1, size);
        } else if (type.equals("solved")) { // 获取已被解决的
            bugPage = bugService.findSolved(page - 1, size);
        } else if (type.equals("my")) { // 获取登陆用户相关的
            String ip = BaseService.getIpAddress(request);
            if (user != null) {
                bugPage = bugService.findMy(ip, user.getId(), page - 1, size);
            } else {
                bugPage = bugService.findMy(ip, -1, page - 1, size);
            }
        }
        List<Bug> bugList = bugPage.getContent();
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
