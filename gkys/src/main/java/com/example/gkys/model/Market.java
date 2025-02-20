package com.example.gkys.model;
import jakarta.persistence.*;


@Entity
@Table(name = "market")
public class Market {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "state_id")
    private State stateId;

    private String name;

    private int price;

    private int value;
}
