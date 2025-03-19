package com.example.gkys.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.gkys.model.StepsModel;

@Repository
public interface StepsRepository extends CrudRepository<StepsModel, Long> {

}
