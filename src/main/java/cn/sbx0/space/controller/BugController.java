package cn.sbx0.space.controller;

import cn.sbx0.space.entity.Bug;
import cn.sbx0.space.service.BaseService;
import cn.sbx0.space.service.BugService;
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
    private ObjectMapper mapper;
    private ObjectNode json;

    @Autowired
    public BugController(ObjectMapper mapper) {
        this.mapper = mapper;
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
    @GetMapping("/list")
    public ArrayNode list(Integer page, Integer size) {
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
            bugService.save(bug);
            json.put(STATUS_NAME, STATUS_CODE_SUCCESS);
        } catch (Exception e) {
            json.put(STATUS_NAME, STATUS_CODE_EXCEPTION);
        }
        return json;
    }

}
