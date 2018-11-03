package cn.sbx0.space.dao;

import cn.sbx0.space.entity.Message;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;
import java.util.List;

/**
 * 消息Dao
 */
public interface MessageDao extends PagingAndSortingRepository<Message, Integer> {

    /**
     * 某时间段的消息
     *
     * @param begin 开始日期
     * @param end   结束日期
     * @return 消息列表
     */
    @Query(value = "SELECT * FROM messages m WHERE m.send_time > ?1 AND m.send_time < ?2", nativeQuery = true)
    List<Message> findByTime(Date begin, Date end);

}