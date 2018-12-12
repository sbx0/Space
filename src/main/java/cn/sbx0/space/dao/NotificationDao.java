package cn.sbx0.space.dao;

import cn.sbx0.space.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * 消息Dao
 */
public interface NotificationDao extends PagingAndSortingRepository<Notification, Integer> {

    /**
     * 根据用户查询消息
     */
    @Query(value = "SELECT n FROM Notification n")
    Page<Notification> findAllByUser(Integer u_id, String type, Pageable pageable);

}