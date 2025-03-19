package com.example.gkys.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.gkys.model.PetItemModel;

@Repository
public interface PetItemRepository extends CrudRepository<PetItemModel, Integer> {

}
