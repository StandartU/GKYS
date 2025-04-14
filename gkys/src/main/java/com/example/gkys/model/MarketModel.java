package com.example.gkys.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "market")
@Setter
@Getter
@NoArgsConstructor
public class MarketModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "state_id")
    private StateModel state;

    private String name;

    private int price;

    private int value;

    private String template;
}
