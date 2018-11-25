package cn.sbx0.space.dao;

import cn.sbx0.space.entity.Url;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 链接Dao
 */
public interface UrlDao extends PagingAndSortingRepository<Url, Integer> {

    /**
     * 设置链接顺序
     */
    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "UPDATE urls AS u SET u.top = ?2 WHERE u.id = ?1")
    void setUrlTopById(Integer id, Integer top);

    /**
     * 查询未被隐藏的链接
     */
    @Query(nativeQuery = true, value = "SELECT * FROM urls AS u WHERE u.page = ?1 AND u.top >= 0 order by u.top ASC")
    List<Url> findByPage(String page);

    /**
     * 查询被隐藏的链接
     */
    @Query(nativeQuery = true, value = "SELECT * FROM urls AS u WHERE u.page = ?1 AND u.top < 0 order by u.top DESC")
    List<Url> findHiddenByPage(String page);

}
