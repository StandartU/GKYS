package com.example.gkys.model;
import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "room")
@Setter
@Getter
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @OneToMany(mappedBy = "roomId", cascade = CascadeType.ALL)
    private List<RoomLvl> roomLvl;

    @OneToMany(mappedBy = "roomId", cascade = CascadeType.ALL)
    private List<UserRoom> userRoom;
}