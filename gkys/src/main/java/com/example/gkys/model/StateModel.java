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

    @OneToMany(mappedBy = "stateId", cascade = CascadeType.ALL)
    private List<MarketModel> market;

    @OneToMany(mappedBy = "stateId", cascade = CascadeType.ALL)
    private List<UserStateModel> userState;

    // Getters and Setters
}