package com.example.gkys.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.gkys.model.ItemModel;


@Repository
public interface ItemRepository extends CrudRepository<ItemModel, Integer> {
    
}
