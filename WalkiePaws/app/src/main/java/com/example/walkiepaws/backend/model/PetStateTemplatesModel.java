package com.example.walkiepaws.backend.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PetStateTemplatesModel {
    private int id;

    private String stateName;

    private PetStateModel petState;
}