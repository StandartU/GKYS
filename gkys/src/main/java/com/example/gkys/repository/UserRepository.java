package com.example.gkys.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.gkys.model.UserModel;


@Repository
public interface UserRepository extends CrudRepository<UserModel, Long> {
    Optional<UserModel> findByLogin(String login);
}
