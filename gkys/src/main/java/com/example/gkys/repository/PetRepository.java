package com.example.gkys.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.gkys.model.PetModel;
import java.util.Optional;


@Repository
public interface PetRepository extends CrudRepository<PetModel, Integer> {
    Optional<PetModel> findByName(String name);
}
