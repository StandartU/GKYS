package com.example.gkys.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "user_state")
@Setter
@Getter
public class UserStateModel {

    public UserStateModel(UserModel user, StateModel state, int value) {
        this.user = user;
        this.state = state;
        this.value = value;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserModel user;

    @ManyToOne
    @JoinColumn(name = "state_id")
    private StateModel state;

    private int value;
}
