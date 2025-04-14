package com.example.gkys.model.dto.responce;

import java.util.List;

import com.example.gkys.model.PetItemModel;

public record GetPetItemDTO(List<PetItemModel> petItemModels) {
    
}
