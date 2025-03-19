package com.example.gkys.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.gkys.model.UserItemModel;
import com.example.gkys.model.UserModel;

@Repository
public interface UserItemRepository extends CrudRepository<UserItemModel, Integer>{
    Iterable<UserItemModel> findAllByUser(UserModel user);
}
