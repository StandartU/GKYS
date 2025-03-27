package com.example.gkys.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.gkys.model.PetModel;
import com.example.gkys.model.PetStateModel;

@Repository
public interface PetStateRepository extends CrudRepository<PetStateModel, Integer> {
    Optional<PetStateModel> findByPetAndLvl(PetModel pet, int lvl);
}
