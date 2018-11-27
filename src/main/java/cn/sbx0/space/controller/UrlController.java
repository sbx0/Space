package cn.sbx0.space.controller;

import cn.sbx0.space.entity.Url;
import cn.sbx0.space.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.List;

/**
 * 评论控制类
 */
@Controller
@RequestMapping("/url")
public class UrlController extends BaseController {
    @Resource
    private UrlService urlService;
    @Resource
    private UserService userService;
    @Resource
    private LogService logService;
    private ObjectMapper mapper;
    private ObjectNode json;

    @Autowired
    public UrlController(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * 更新链接顺序
     */
    @ResponseBody
    @PostMapping("/update")
    public ObjectNode update(HttpServletRequest request) {
        String json = BaseService.getJson(request);
        try {
            JSONObject jsonObject = new JSONObject(json);
            String show = jsonObject.getString("show");
            String hide = jsonObject.getString("hide");
            JSONObject showJson = new JSONObject(show);
            Iterator iterator = showJson.keys();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                Integer value = Integer.parseInt(showJson.getString(key));
                urlService.setUrlTopById(Integer.parseInt(key), value);
            }
            JSONObject hideJson = new JSONObject(hide);
            iterator = hideJson.keys();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                Integer value = Integer.parseInt(hideJson.getString(key));
                urlService.setUrlTopById(Integer.parseInt(key), value);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.json = mapper.createObjectNode();
        return this.json;
    }

    /**
     * 获取指定页面的链接
     * 比如导航条的链接
     * 比如工具页的链接
     * 支持自定义排序
     */
    @ResponseBody
    @GetMapping("/get")
    public ArrayNode get(String page) {
        ArrayNode arrayNode = mapper.createArrayNode();
        List<Url> urlList = urlService.findByPage(page);
        for (Url url : urlList) {
            json = mapper.createObjectNode();
            json.put("id", url.getId());
            json.put("text", url.getText());
            json.put("title", url.getTitle());
            json.put("path", url.getPath());
            json.put("badge", url.getBadge());
            json.put("top", url.getTop());
            arrayNode.add(json);
        }
        return arrayNode;
    }

    @ResponseBody
    @GetMapping("/getHidden")
    public ArrayNode getHidden(String page) {
        ArrayNode arrayNode = mapper.createArrayNode();
        List<Url> urlList = urlService.findHiddenByPage(page);
        for (Url url : urlList) {
            json = mapper.createObjectNode();
            json.put("id", url.getId());
            json.put("text", url.getText());
            json.put("title", url.getTitle());
            json.put("path", url.getPath());
            json.put("badge", url.getBadge());
            json.put("top", url.getTop());
            arrayNode.add(json);
        }
        return arrayNode;
    }

}
