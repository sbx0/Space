package cn.sbx0.space.dao;

import cn.sbx0.space.entity.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;
import java.util.List;

/**
 * 日志Dao
 */
public interface LogDao extends PagingAndSortingRepository<Log, Integer> {

    /**
     * 统计某时间段的日志
     *
     * @param begin
     * @param end
     * @return
     */
    @Query(value = "select * from logs l where l.time > ?1 and l.time < ?2", nativeQuery = true)
    List<Log> countByTime(Date begin, Date end);

    /**
     * 查询某ip的最近limit次操作
     *
     * @param ip
     * @return
     */
    @Query(value = "select  * from logs l where l.ip = ?1 and l.method = ?2 and l.query = ?3 order by l.id desc limit ?4", nativeQuery = true)
    List<Log> findByIpAndMethodAndQuery(String ip, String method, String query, Integer limit);

    /**
     * 查询某ip的最近limit次操作
     *
     * @param ip
     * @return
     */
    @Query(value = "select  * from logs l where l.ip = ?1 order by l.id desc limit ?2", nativeQuery = true)
    List<Log> findByIpAndUrl(String ip, Integer limit);

    /**
     * 查询某ip的某个方法的最近limit次操作
     *
     * @param ip
     * @param method
     * @return
     */
    @Query(value = "select  * from logs l where l.ip = ?1 and l.method = ?2 order by l.id desc limit ?3", nativeQuery = true)
    List<Log> findByIpAndMethod(String ip, String method, Integer limit);

    /**
     * 查询日志列表
     */
    @Query(value = "select  * from logs", nativeQuery = true)
    Page<Log> findAll(Pageable pageable);
}
