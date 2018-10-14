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
     * 查询某ip的某个方法的最近一次操作
     *
     * @param ip
     * @param method
     * @return
     */
    @Query(value = "select  * from logs l where l.ip = ?1 and l.method = ?2 order by l.time desc limit 1", nativeQuery = true)
    Log findByIpAndEvent(String ip, String method);

    /**
     * 查询日志列表
     */
    @Query(value = "select  * from logs", nativeQuery = true)
    Page<Log> findAll(Pageable pageable);
}
