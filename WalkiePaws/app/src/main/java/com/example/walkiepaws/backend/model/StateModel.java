package com.example.walkiepaws.backend.model;
import java.util.List;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class StateModel {

    private int id;

    private String name;


    private List<MarketModel> markets;


    private List<UserStateModel> userStates;

    // Getters and Setters
}