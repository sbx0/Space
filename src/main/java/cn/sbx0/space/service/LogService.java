package cn.sbx0.space.service;

import cn.sbx0.space.dao.LogDao;
import cn.sbx0.space.entity.Log;
import cn.sbx0.space.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用户服务层
 */
@Service
public class LogService extends BaseService {
    @Resource
    private LogDao logDao;

    /**
     * 检测重复操作
     *
     * @param request
     * @param minutes 限制时间
     * @return
     */
    public boolean check(HttpServletRequest request, int minutes) {
        String ip = getIpAddress(request); // 用户IP
        String method = request.getServletPath(); // 运行的方法
        String query = request.getQueryString(); // 参数
        List<Log> logs = new ArrayList<>();
        if (!BaseService.checkNullStr(query)) { // 参数不为空
            if (method.equals("/article/dislike") && !query.equals("")) // 若踩则找赞
                logs = logDao.findByIpAndMethodAndQuery(ip, "/article/like", query, 1);
            if (method.equals("/article/like") && !query.equals("")) // 若赞则找踩
                logs = logDao.findByIpAndMethodAndQuery(ip, "/article/dislike", request.getQueryString(), 1);
            if (logs.size() > 0) return false; // 同一片文章不能同时点赞点踩
            logs = logDao.findByIpAndMethodAndQuery(ip, method, query, 1); // 查看上一次相同操作的记录
        } else logs = logDao.findByIpAndMethod(ip, method, 1); // 查看上一次相同操作的记录
        if (logs.size() == 0) return true; // 如果没有则直接检测通过
        // 开始检测前一次与这一次的分钟差是否达到要求
        Date prevTime = logs.get(0).getTime();
        Date nowTime = new Date();
        long prev = prevTime.getTime();
        long now = nowTime.getTime();
        int m = (int) ((now - prev) / (1000 * 60));
        if (m > minutes) return true;
        else return false;
    }

    /**
     * 查询全部
     *
     * @param page
     * @param size
     * @return
     */
    public Page<Log> findAll(Integer page, Integer size) {
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
            return logDao.findAll(pageable);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 保存日志
     *
     * @param log
     * @return 操作成功与否
     */
    @Transactional
    public boolean save(Log log) {
        try {
            logDao.save(log);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 公用方法

    /**
     * 获取当前操作的一系列数据
     *
     * @return
     */
    public boolean log(User user, HttpServletRequest request) {
        // Log
        Log log = new Log();
        // 记录ip
        log.setIp(getIpAddress(request));
        log.setUser(user);
        log.setTime(new Date());
        if (request.getQueryString() != null) {
            log.setQuery(request.getQueryString());
            log.setUrl(request.getRequestURL().toString() + "?" + request.getQueryString());
        } else
            log.setUrl(request.getRequestURL().toString());
        log.setMethod(request.getServletPath());

        // 刷新不记录log
        List<Log> logs = logDao.findByIpAndUrl(log.getIp(), 1);
        if (logs.size() > 0 && logs.get(0).getUrl().equals(log.getUrl()))
            return false;

        if (log == null)
            return true;
        try {
            save(log);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
