package com.example.gkys.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.gkys.model.StateModel;
import com.example.gkys.model.UserModel;
import com.example.gkys.model.UserStateModel;

@Repository
public interface UserStateRepository extends CrudRepository<UserStateModel, Long> {
    Optional<UserStateModel> findByUserAndState(UserModel user, StateModel state);

    Iterable<UserStateModel> findAllByUser(UserModel user);

    Iterable<UserStateModel> findAllByState(StateModel state);
}
