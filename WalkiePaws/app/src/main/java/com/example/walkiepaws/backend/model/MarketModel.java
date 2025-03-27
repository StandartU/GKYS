package com.example.walkiepaws.backend.model;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class MarketModel {

    private int id;

    private StateModel state;

    private String name;

    private int price;

    private int value;
}
