package com.github.zzbslayer.vnfdemo.repo;

import com.github.zzbslayer.vnfdemo.entity.Dblock;
import com.github.zzbslayer.vnfdemo.entity.History;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DblockRepository extends CrudRepository<Dblock, Integer> {
}
