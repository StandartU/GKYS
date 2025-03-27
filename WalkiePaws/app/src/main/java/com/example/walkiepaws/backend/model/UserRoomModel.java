package com.example.walkiepaws.backend.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserRoomModel {

    public UserRoomModel(UserModel user, RoomModel room, int lvl) {
        this.user = user;
        this.room = room;
        this.lvl = lvl;
    }


    private int id;


    private UserModel user;


    private RoomModel room;

    private int lvl;
}