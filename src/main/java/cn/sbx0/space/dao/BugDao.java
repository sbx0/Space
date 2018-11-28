package cn.sbx0.space.dao;

import cn.sbx0.space.entity.Bug;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

/**
 * 反馈Dao
 */
public interface BugDao extends PagingAndSortingRepository<Bug, Integer> {

    /**
     * 根据ID寻找未被隐藏的反馈
     */
    @Query(nativeQuery = true, value = "SELECT * FROM bugs AS b WHERE b.top >= 0 AND b.id = ?1")
    Optional<Bug> findById(Integer id);

    /**
     * 查询未被隐藏的反馈
     */
    @Query(nativeQuery = true, value = "SELECT * FROM bugs AS b WHERE b.top >= 0")
    Page<Bug> findAll(Pageable pageable);
}
