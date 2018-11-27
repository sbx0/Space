package cn.sbx0.space.service;

import cn.sbx0.space.dao.BugDao;
import cn.sbx0.space.entity.Bug;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class BugService extends BaseService {
    @Resource
    private BugDao bugDao;

    /**
     * 分页查询未被隐藏的反馈列表
     */
    public Page<Bug> findAll(Integer page, Integer size) {
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
            // 分页查询
            Page<Bug> bugPage = bugDao.findAll(pageable);
            return bugPage;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 保存文章
     */
    @Transactional
    public boolean save(Bug bug) {
        try {
            bugDao.save(bug);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
