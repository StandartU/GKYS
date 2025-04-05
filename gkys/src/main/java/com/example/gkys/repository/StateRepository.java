package com.example.gkys.repository;


import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.gkys.model.StateModel;

@Repository
public interface StateRepository extends CrudRepository<StateModel, Integer> {
    Optional<StateModel> findByName(String name);
}
