package com.example.gkys.model;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pet_state")
@Setter
@Getter
@NoArgsConstructor
public class PetStateModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int lvl;

    @JsonIgnore
    @OneToMany(mappedBy = "petState", cascade = CascadeType.ALL)
    private List<PetStateTemplatesModel> template;

    @ManyToOne
    @JoinColumn(name = "pet_id")
    private PetModel pet;
}
