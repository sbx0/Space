package cn.sbx0.space.controller;

import cn.sbx0.space.entity.Url;
import cn.sbx0.space.entity.User;
import cn.sbx0.space.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.List;

/**
 * 评论控制类
 */
@Controller
@RequestMapping("/url")
public class UrlController extends BaseController<Url, Integer> {
    @Autowired
    private UrlService urlService;
    @Resource
    private UserService userService;

    @Override
    public BaseService<Url, Integer> getService() {
        return urlService;
    }

    @Autowired
    public UrlController(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * 添加链接
     */
    @ResponseBody
    @PostMapping("/saveOrUpdate")
    public ObjectNode saveOrUpdate(@ModelAttribute Url url, BindingResult b, HttpServletRequest request) {
        User user = userService.getCookieUser(request);
        json = mapper.createObjectNode();
        if (user != null) {
            if (user.getAuthority() == 0) {
                if (!BaseService.checkNullStr(url.getPath()) && !BaseService.checkNullStr(url.getText())) {
                    url.setTop(0);
                    if (urlService.save(url)) {
                        json.put(STATUS_NAME, STATUS_CODE_SUCCESS);
                    } else {
                        json.put(STATUS_NAME, STATUS_CODE_FILED);
                    }
                } else {
                    json.put(STATUS_NAME, STATUS_CODE_NOT_FOUND);
                }
            } else {
                json.put(STATUS_NAME, STATUS_CODE_NO_PERMISSION);
            }
        } else {
            json.put(STATUS_NAME, STATUS_CODE_NOT_LOGIN);
        }
        return json;
    }

    /**
     * 更新链接顺序
     */
    @ResponseBody
    @PostMapping("/update")
    public ObjectNode update(HttpServletRequest request) {
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
        String jsonString = BaseService.getJson(request);
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
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
            json.put(STATUS_NAME, STATUS_CODE_SUCCESS);
        } catch (JSONException e) {
            json.put(STATUS_NAME, STATUS_CODE_EXCEPTION);
            json.put("msg", e.getMessage());
        }
        return json;
    }

    /**
     * 获取指定页面的链接
     * 比如导航条的链接
     * 比如工具页的链接
     * 支持自定义排序
     */
    @ResponseBody
    @GetMapping("/get")
    public ObjectNode get(String page) {
        json = mapper.createObjectNode();
        ArrayNode jsons = mapper.createArrayNode();
        List<Url> urlList = urlService.findByPage(page);
        for (Url url : urlList) {
            ObjectNode temp = mapper.createObjectNode();
            temp.put("id", url.getId());
            temp.put("text", url.getText());
            temp.put("title", url.getTitle());
            temp.put("path", url.getPath());
            temp.put("badge", url.getBadge());
            temp.put("top", url.getTop());
            jsons.add(temp);
        }
        json.set("urls", jsons);
        return json;
    }

    /**
     * 获取隐藏的链接
     */
    @ResponseBody
    @GetMapping("/getHidden")
    public ObjectNode getHidden(String page) {
        json = mapper.createObjectNode();
        ArrayNode jsons = mapper.createArrayNode();
        List<Url> urlList = urlService.findHiddenByPage(page);
        for (Url url : urlList) {
            ObjectNode temp = mapper.createObjectNode();
            temp.put("id", url.getId());
            temp.put("text", url.getText());
            temp.put("title", url.getTitle());
            temp.put("path", url.getPath());
            temp.put("badge", url.getBadge());
            temp.put("top", url.getTop());
            jsons.add(temp);
        }
        json.set("urls", jsons);
        return json;
    }

}
