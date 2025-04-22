package com.example.gkys.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.gkys.model.TasksModel;
import com.example.gkys.model.UserModel;
import com.example.gkys.model.UserTaskModel;

@Repository
public interface UserTaskRepository extends CrudRepository<UserTaskModel, Long>{
    Iterable<UserTaskModel> findAllByUser(UserModel user);

    Iterable<UserTaskModel> findAllByTask(TasksModel task);

    Optional<UserTaskModel> findByUserAndTask(UserModel user, TasksModel task);
}
