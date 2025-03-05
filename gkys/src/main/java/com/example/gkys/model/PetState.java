package com.example.gkys.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "pet_state")
@Setter
@Getter
public class PetState {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "pet_id")
    private Pet petId;

    private int lvl;

    private String template;
}
