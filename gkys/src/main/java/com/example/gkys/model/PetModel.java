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

    @OneToMany(mappedBy = "petId", cascade = CascadeType.ALL)
    private List<PetStateModel> petState;

    @OneToMany(mappedBy = "petId", cascade = CascadeType.ALL)
    private List<PetItemModel> petItem;

    private String name;

}