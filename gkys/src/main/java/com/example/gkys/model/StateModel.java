package com.example.gkys.model;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "state")
@Setter
@Getter
@NoArgsConstructor
public class StateModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "state", cascade = CascadeType.ALL)
    private List<MarketModel> markets;

    @JsonIgnore
    @OneToMany(mappedBy = "state", cascade = CascadeType.ALL)
    private List<UserStateModel> userStates;

    // Getters and Setters
}