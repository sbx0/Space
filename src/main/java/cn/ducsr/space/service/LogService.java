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
    public boolean log(User user, HttpServletRequest httpServletRequest, boolean status) {
        Log log = new Log();
        log.setTime(new Date());
        log.setUser(user);
        log.setIp(BaseService.getIpAddress(httpServletRequest));
        log.setEvent(httpServletRequest.getQueryString());
        log.setMethod(httpServletRequest.getRequestURI());
        log.setStatus(status);
        try {
            logDao.save(log);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
