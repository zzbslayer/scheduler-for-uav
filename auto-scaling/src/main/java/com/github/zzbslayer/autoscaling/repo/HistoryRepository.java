package com.github.zzbslayer.autoscaling.repo;

import com.github.zzbslayer.autoscaling.entity.History;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoryRepository extends CrudRepository<History, Integer> {
    @Query(value = "select * from (select * from history where name=:name order by id DESC limit :n) sub order by id asc", nativeQuery = true)
    List<History> findLastNHistoryByName(@Param("name") String name, @Param("n") int n);
}
