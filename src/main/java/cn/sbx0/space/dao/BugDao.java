package cn.sbx0.space.dao;

import cn.sbx0.space.entity.Bug;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * 反馈Dao
 */
public interface BugDao extends PagingAndSortingRepository<Bug, Integer> {

    /**
     * 查询未被隐藏的反馈
     */
    @Query(nativeQuery = true, value = "SELECT * FROM bugs AS b WHERE b.top >= 0")
    Page<Bug> findAll(Pageable pageable);
}
