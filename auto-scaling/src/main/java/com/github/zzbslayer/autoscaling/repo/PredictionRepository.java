package com.github.zzbslayer.autoscaling.repo;

import com.github.zzbslayer.autoscaling.entity.Prediction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PredictionRepository extends CrudRepository<Prediction, Integer> {

}
