package cn.sbx0.space.service;

import cn.sbx0.space.dao.UrlDao;
import cn.sbx0.space.entity.Url;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 链接服务层
 */
@Service
public class UrlService extends BaseService<Url,Integer> {
    @Autowired
    private UrlDao urlDao;

    @Override
    public PagingAndSortingRepository<Url, Integer> getDao() {
        return urlDao;
    }

    /**
     * 设置链接顺序
     */
    public boolean setUrlTopById(Integer id, Integer top) {
        try {
            urlDao.setUrlTopById(id, top);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 根据页面查找链接
     */
    public List<Url> findByPage(String page) {
        if (BaseService.checkNullStr(page))
            return null;
        return urlDao.findByPage(page);
    }

    /**
     * 根据页面查找被隐藏链接
     */
    public List<Url> findHiddenByPage(String page) {
        if (BaseService.checkNullStr(page))
            return null;
        return urlDao.findHiddenByPage(page);
    }

}
