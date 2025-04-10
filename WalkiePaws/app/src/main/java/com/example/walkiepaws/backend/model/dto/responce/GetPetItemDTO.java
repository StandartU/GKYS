package com.example.walkiepaws.backend.model.dto.responce;

import com.example.walkiepaws.backend.model.PetItemModel;

import java.util.List;


public record GetPetItemDTO(List<PetItemModel> petItemModels) { }