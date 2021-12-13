package com.github.zzbslayer.gateway.repo;

import com.github.zzbslayer.gateway.entity.Dblock;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DblockRepository extends CrudRepository<Dblock, Integer> {
}
