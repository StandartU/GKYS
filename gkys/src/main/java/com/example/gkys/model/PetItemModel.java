package com.example.gkys.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "pet_item")
@Setter
@Getter
public class PetItemModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "pet_id")
    private PetModel pet;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private ItemModel item;

    private String template;

}