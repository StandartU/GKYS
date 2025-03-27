package com.example.walkiepaws.backend.model;

import java.util.List;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class PetModel {

    private int id;

    private String name;

    private List<PetStateModel> petStates;

    private List<PetItemModel> petItems;
}