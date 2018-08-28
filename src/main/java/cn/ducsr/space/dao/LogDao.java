package cn.ducsr.space.dao;

import cn.ducsr.space.entity.Log;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * 日志Dao
 */
public interface LogDao extends PagingAndSortingRepository<Log, Integer> {
}
