package com.example.gkys.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


import com.example.gkys.model.RoomModel;
import java.util.Optional;


@Repository
public interface RoomRepository extends CrudRepository<RoomModel, Integer>{
    Optional<RoomModel> findByName(String name);
}
