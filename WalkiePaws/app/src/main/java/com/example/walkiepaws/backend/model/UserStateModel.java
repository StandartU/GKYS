package com.example.walkiepaws.backend.model;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class UserStateModel {

    public UserStateModel(UserModel user, StateModel state, int value) {
        this.user = user;
        this.state = state;
        this.value = value;
    }


    private Long id;


    private UserModel user;


    private StateModel state;

    private int value;
}
