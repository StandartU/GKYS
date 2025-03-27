package com.example.gkys.model;
import java.util.List;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "pet")
@Setter
@Getter
public class PetModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL)
private List<PetStateModel> petStates;

    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL)
    private List<PetItemModel> petItems;
}