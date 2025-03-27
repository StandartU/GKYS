package com.example.walkiepaws.backend.model;
import java.util.List;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class PetStateModel {
    private int id;

    private int lvl;

    private List<PetStateTemplatesModel> template;

    private PetModel pet;
}
