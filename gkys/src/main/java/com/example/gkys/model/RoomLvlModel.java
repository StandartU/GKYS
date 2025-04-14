package com.example.gkys.model;
import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "room_lvl")
@Setter
@Getter
@NoArgsConstructor
public class RoomLvlModel {

    public RoomLvlModel(RoomModel room, int lvl, List<String> templates, int price) {
        this.room = room;
        this.lvl = lvl;
        this.templates = templates;
        this.price = price;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private RoomModel room;

    private int lvl;

    private List<String> templates;

    private String marketTemplate;

    private int price;
}