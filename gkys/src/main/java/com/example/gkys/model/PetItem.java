package com.example.gkys.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "pet_item")
@Setter
@Getter
public class PetItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "pet_id")
    private Pet petId;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item itemId;

    private String template;

}