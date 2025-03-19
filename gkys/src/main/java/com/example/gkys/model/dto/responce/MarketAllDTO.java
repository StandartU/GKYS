package com.example.gkys.model.dto.responce;

import com.example.gkys.model.ItemModel;
import com.example.gkys.model.MarketModel;
import com.example.gkys.model.RoomLvlModel;

public record MarketAllDTO(Iterable<MarketModel> marketModels, Iterable<RoomLvlModel> roomModels, Iterable<ItemModel> itemModels) {}
