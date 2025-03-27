package com.example.walkiepaws.backend.model;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserModel {


    private Long id;


    private String login;


    private String password;

    private int cash;

    public UserModel(String login, String password) {
        this.login = login;
        this.password = password;
    }

    private List<StepsModel> steps;

    private List<UserItemModel> userItems;

    private List<UserRoomModel> userRooms;

    
}
