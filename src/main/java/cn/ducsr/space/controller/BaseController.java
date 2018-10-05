package cn.ducsr.space.controller;

import cn.ducsr.space.service.BaseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;

@Controller
public class BaseController {
	// 为了返回json
	ObjectMapper mapper = new ObjectMapper();
	ObjectNode objectNode = mapper.createObjectNode();
}
