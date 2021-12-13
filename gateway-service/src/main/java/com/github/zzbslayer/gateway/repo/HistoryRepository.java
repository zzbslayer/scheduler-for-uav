package com.github.zzbslayer.gateway.repo;

import com.github.zzbslayer.gateway.entity.History;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HistoryRepository extends CrudRepository<History, Integer> {
    @Query(value = "select * from history where name=:name and create_time=:createTime limit 1", nativeQuery = true)
    Optional<History> findHistoryByNameAndTime(@Param("name") String name, @Param("createTime") String createTime);
}
