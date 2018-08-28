package cn.ducsr.space.dao;

import cn.ducsr.space.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * 用户Dao
 */
public interface UserDao extends PagingAndSortingRepository<User, Integer> {
    @Query(value = "select 1 from users where name = ?1", nativeQuery = true)
    String existsByName(String name);

    @Query(value = "select * from users where name = ?1", nativeQuery = true)
    User findByName(String name);
}
