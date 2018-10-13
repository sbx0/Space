package cn.ducsr.space.dao;

import cn.ducsr.space.entity.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * 日志Dao
 */
public interface LogDao extends PagingAndSortingRepository<Log, Integer> {
    /**
     * 查询日志列表
     */
    @Query(value = "select  * from logs", nativeQuery = true)
    Page<Log> findAll(Pageable pageable);
}
