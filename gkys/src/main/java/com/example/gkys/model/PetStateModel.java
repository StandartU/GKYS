package com.example.gkys.model;
import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "pet_state")
@Setter
@Getter
public class PetStateModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int lvl;

    @OneToMany(mappedBy = "petState", cascade = CascadeType.ALL)
    private List<PetStateTemplatesModel> template;

    @ManyToOne
    @JoinColumn(name = "pet_id")
    private PetModel pet;
}
