package com.example.walkiepaws.backend.model;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class StepsModel {

    private Long id;
    

    private UserModel user;
    

    private Date date;

    private int steps;

    public StepsModel(UserModel userModel, int steps) {
        this.user = userModel;
        this.steps = steps;
    }
}
