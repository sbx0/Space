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
     * 某个时间段统计访问ip数
     *
     * @param begin 开始日期
     * @param end   结束日期
     * @return 包含日期(2018 - 08 - 01)和对应的访问IP条数(10)
     */
    @Query(value = " SELECT CAST(l.time AS DATE),COUNT(DISTINCT l.ip) AS number FROM logs l WHERE l.time > ?1 AND l.time < ?2 GROUP BY CAST(l.time AS DATE)", nativeQuery = true)
    List<Object[]> countIpByTime(Date begin, Date end);

    /**
     * 统计某时间段的日志
     *
     * @param begin 开始日期
     * @param end   结束日期
     * @return 日志列表
     */
    @Query(value = "SELECT * FROM logs l WHERE l.time > ?1 AND l.time < ?2", nativeQuery = true)
    List<Log> countByTime(Date begin, Date end);

    /**
     * 根据方法名查询参数查询某ip的最近limit次操作
     *
     * @param ip     IP
     * @param method 方法名
     * @param query  查询参数
     * @param limit  限制返回条数
     * @return 对应limit条日志
     */
    @Query(value = "SELECT * FROM logs l WHERE l.ip = ?1 AND l.method = ?2 AND l.query = ?3 ORDER BY l.id DESC limit ?4", nativeQuery = true)
    List<Log> findByIpAndMethodAndQuery(String ip, String method, String query, Integer limit);

    /**
     * 查询某ip的最近limit次操作
     *
     * @param ip    IP
     * @param limit 限制返回条数
     * @return 对应limit条日志
     */
    @Query(value = "SELECT * FROM logs l WHERE l.ip = ?1 ORDER BY l.id DESC limit ?2", nativeQuery = true)
    List<Log> findByIpAndUrl(String ip, Integer limit);

    /**
     * 根据方法名查询某ip的某个方法的最近limit次操作
     *
     * @param ip     IP
     * @param method 方法名
     * @param limit  限制返回条数
     * @return 对应limit条日志
     */
    @Query(value = "SELECT * FROM logs l WHERE l.ip = ?1 AND l.method = ?2 ORDER BY l.id DESC limit ?3", nativeQuery = true)
    List<Log> findByIpAndMethod(String ip, String method, Integer limit);

    /**
     * 查询全部日志
     *
     * @param pageable 分页查询
     * @return 对应页数和条数的日志列表
     */
    @Query(value = "SELECT * FROM logs", nativeQuery = true)
    Page<Log> findAll(Pageable pageable);
}
