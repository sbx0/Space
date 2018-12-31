package cn.sbx0.space.service;

import cn.sbx0.space.dao.BugDao;
import cn.sbx0.space.entity.Bug;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class BugService extends BaseService<Bug, Integer> {
    @Resource
    private BugDao bugDao;

    @Override
    public PagingAndSortingRepository<Bug, Integer> getDao() {
        return bugDao;
    }

    /**
     * 查询已被解决的反馈
     */
    public Page<Bug> findSolved(Integer page, Integer size) {
        Pageable pageable = buildPageable(page, size, buildSort("id", "DESC"));
        try {
            // 分页查询
            return bugDao.findSolved(pageable);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 寻找我提交的反馈
     */
    public Page<Bug> findMy(String ip, Integer submitter_id, Integer page, Integer size) {
        Pageable pageable = buildPageable(page, size, buildSort("id", "DESC"));
        try {
            // 分页查询
            return bugDao.findMy(ip, submitter_id, pageable);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 分页查询未被隐藏的反馈列表
     */
    public Page<Bug> findAll(Integer page, Integer size) {
        Pageable pageable = buildPageable(page, size, buildSort("id", "DESC"));
        try {
            // 分页查询
            return bugDao.findAll(pageable);
        } catch (Exception e) {
            return null;
        }
    }

}
