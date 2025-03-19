package com.example.gkys.model;
import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "room")
@Setter
@Getter
public class RoomModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<RoomLvlModel> roomLvls;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<UserRoomModel> userRooms;
}