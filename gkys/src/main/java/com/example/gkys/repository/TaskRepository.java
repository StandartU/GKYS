package com.example.gkys.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.gkys.model.TasksModel;

@Repository
public interface TaskRepository extends CrudRepository<TasksModel, Integer> {    
    Optional<TasksModel> findByName(String name);
}

