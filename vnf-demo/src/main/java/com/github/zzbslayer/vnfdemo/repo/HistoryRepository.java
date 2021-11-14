package com.github.zzbslayer.vnfdemo.repo;

import com.github.zzbslayer.vnfdemo.entity.History;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoryRepository extends CrudRepository<History, Integer> {
    @Query(value = "select * from (select * from history where name='vnf-a' order by id DESC limit 5) sub order by id asc", nativeQuery = true)
    List<History> findLastNHistoryByName(@Param("name") String name);
}
