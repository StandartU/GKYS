package com.example.gkys.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pet_item")
@Setter
@Getter
@NoArgsConstructor
public class PetItemModel {

    public PetItemModel(int lvl, PetModel pet, ItemModel item, String template) {
        this.template = template;
        this.lvl = lvl;
        this.pet = pet;
        this.item = item;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "pet_id")
    private PetModel pet;

    private int lvl;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private ItemModel item;

    private String template;

}