package com.example.gkys.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.gkys.model.RoomLvlModel;
import com.example.gkys.model.RoomModel;

@Repository
public interface RoomLvlRepository extends CrudRepository<RoomLvlModel, Integer> {
    Optional<RoomLvlModel> findByRoomAndLvl(RoomModel room, int lvl);
}
