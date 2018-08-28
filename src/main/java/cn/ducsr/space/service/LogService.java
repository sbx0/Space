package cn.ducsr.space.service;

import cn.ducsr.space.dao.LogDao;
import cn.ducsr.space.entity.Log;
import cn.ducsr.space.entity.User;
import org.omg.PortableInterceptor.USER_EXCEPTION;
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
