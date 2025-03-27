package com.example.walkiepaws.backend.model.dto.responce;

import com.example.walkiepaws.backend.model.ItemModel;
import com.example.walkiepaws.backend.model.MarketModel;
import com.example.walkiepaws.backend.model.RoomLvlModel;

public record MarketAllDTO(Iterable<MarketModel> marketModels, Iterable<RoomLvlModel> roomModels, Iterable<ItemModel> itemModels) {}
