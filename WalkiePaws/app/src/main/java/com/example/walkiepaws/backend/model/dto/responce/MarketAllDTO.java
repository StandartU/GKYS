package com.example.walkiepaws.backend.model.dto.responce;

import com.example.walkiepaws.backend.model.ItemModel;
import com.example.walkiepaws.backend.model.MarketModel;
import com.example.walkiepaws.backend.model.RoomLvlModel;

import java.util.List;

public record MarketAllDTO(List<MarketModel> marketModels, List<RoomLvlModel> roomModels, List<ItemModel> itemModels) {}
