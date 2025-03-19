package com.example.gkys.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.gkys.model.RoomModel;
import com.example.gkys.model.UserModel;
import com.example.gkys.model.UserRoomModel;

@Repository
public interface UserRoomRepository extends CrudRepository<UserRoomModel, Integer> {
    Iterable<UserRoomModel> findAllByUser (UserModel user);

    Optional<UserRoomModel> findByUserAndRoom(UserModel user, RoomModel room);
}
