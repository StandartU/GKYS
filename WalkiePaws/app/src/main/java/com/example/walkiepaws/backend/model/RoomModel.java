package com.example.walkiepaws.backend.model;
import java.util.List;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class RoomModel {

    private int id;

    private String name;


    private List<RoomLvlModel> roomLvls;


    private List<UserRoomModel> userRooms;
}