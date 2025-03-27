package com.example.gkys.model;
import java.util.List;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "state")
@Setter
@Getter
public class StateModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @OneToMany(mappedBy = "state", cascade = CascadeType.ALL)
    private List<MarketModel> markets;

    @OneToMany(mappedBy = "state", cascade = CascadeType.ALL)
    private List<UserStateModel> userStates;

    // Getters and Setters
}