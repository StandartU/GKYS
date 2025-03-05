package com.example.gkys.model;
import java.util.List;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity

@Table(name = "room_lvl")
@Setter
@Getter
public class RoomLvl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "room_id")
    
    private Room roomId;

    private int lvl;

    @ElementCollection
    @CollectionTable(name = "templates", joinColumns = @JoinColumn(name = "room_id"))
    @Column(name = "template")
    private List<String> templates;


    // Getters and Setters

}