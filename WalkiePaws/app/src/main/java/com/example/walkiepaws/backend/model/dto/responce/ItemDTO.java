package com.example.walkiepaws.backend.model.dto.responce;

import com.example.walkiepaws.backend.model.ItemModel;

import java.util.List;

public record ItemDTO(List<ItemModel> items) {
}
