package cn.sbx0.space.dao;

import cn.sbx0.space.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * 用户Dao
 */
public interface UserDao extends PagingAndSortingRepository<User, Integer> {
    /**
     * 查询某用户名是否存在
     *
     * @param name 用户名
     * @return 有结果则存在 无则不存在
     */
    @Query(value = "SELECT 1 FROM users u WHERE u.name = ?1", nativeQuery = true)
    String existsByName(String name);

    /**
     * 根据用户名查询用户
     *
     * @param name 用户名
     * @return 用户
     */
    @Query(value = "SELECT * FROM users u WHERE u.name = ?1", nativeQuery = true)
    User findByName(String name);

    /**
     * 站点地图
     * 查询所有用户
     *
     * @return 用户列表
     */
    @Query(value = "SELECT * FROM users", nativeQuery = true)
    List<User> findAll();
}
