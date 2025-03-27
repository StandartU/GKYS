package com.example.walkiepaws.backend.model;
import java.util.List;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class RoomLvlModel {

    private int id;

    private RoomModel room;

    private int lvl;

    private List<String> templates;

    private int price;
}