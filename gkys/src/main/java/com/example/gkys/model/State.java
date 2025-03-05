package com.example.gkys.model;
import java.util.List;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "state")
@Setter
@Getter
public class State {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @OneToMany(mappedBy = "stateId", cascade = CascadeType.ALL)
    private List<Market> market;

    @OneToMany(mappedBy = "stateId", cascade = CascadeType.ALL)
    private List<UserState> userState;

    // Getters and Setters
}