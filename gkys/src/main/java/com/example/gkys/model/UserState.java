package com.example.gkys.model;
import jakarta.persistence.*;


@Entity
@Table(name = "user_state")
public class UserState {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userId;

    @ManyToOne
    @JoinColumn(name = "state_id")
    private State stateId;

    private int value;
}
