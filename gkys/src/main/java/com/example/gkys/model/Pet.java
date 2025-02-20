package com.example.gkys.model;
import java.util.List;
import jakarta.persistence.*;


@Entity
@Table(name = "pet")
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToMany(mappedBy = "petId", cascade = CascadeType.ALL)
    private List<PetState> petState;

    @OneToMany(mappedBy = "petId", cascade = CascadeType.ALL)
    private List<PetItem> petItem;

    private String name;

}