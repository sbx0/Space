package cn.ducsr.space.service;

import cn.ducsr.space.dao.LogDao;
import cn.ducsr.space.entity.Log;
import cn.ducsr.space.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

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
     * @param minutes 事件
     * @return
     */
    public boolean check(HttpServletRequest request, int minutes) {
        String ip = BaseService.getIpAddress(request);
        String method = request.getServletPath();
        String query = request.getQueryString();
        Log log;
        if (!query.equals("")) {
            if (method.equals("/article/dislike") && !query.equals("")) {
                log = logDao.findByIpAndMethodAndQuery(ip, "/article/like", query);
                if (log != null)
                    return false;
            }
            if (method.equals("/article/like") && !query.equals("")) {
                log = logDao.findByIpAndMethodAndQuery(ip, "/article/dislike", request.getQueryString());
                if (log != null)
                    return false;
            }
            log = logDao.findByIpAndMethodAndQuery(ip, method, query);
        } else
            log = logDao.findByIpAndEvent(ip, method);
        if (log == null)
            return true;

        Date time = log.getTime();
        Date now = new Date();
        long past = time.getTime();
        long to = now.getTime();
        int m = (int) ((to - past) / (1000 * 60));
        if (m > minutes)
            return true;
        else
            return false;
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
        // 不记录管理员的操作
        if (log.getUser() != null && log.getUser().getAuthority() == 0)
            return false;
        else {
            try {
                logDao.save(log);
                return true;
            } catch (Exception e) {
                return false;
            }
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
        log.setIp(BaseService.getIpAddress(request));
        log.setUser(user);
        log.setTime(new Date());
        if (request.getQueryString() != null) {
            log.setQuery(request.getQueryString());
            log.setUrl(request.getRequestURL().toString() + "?" + request.getQueryString());
        } else
            log.setUrl(request.getRequestURL().toString());
        log.setMethod(request.getServletPath());

        // 刷新不记录log
        Log prevLog = logDao.findByIpAndUrl(log.getIp());
        if (prevLog != null && prevLog.getUrl().equals(log.getUrl()))
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
