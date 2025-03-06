package com.example.gkys.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.gkys.model.UserModel;


@Repository
public interface UserRepository extends JpaRepository<UserModel, Long> {
    UserModel findByLogin(String login);
}
