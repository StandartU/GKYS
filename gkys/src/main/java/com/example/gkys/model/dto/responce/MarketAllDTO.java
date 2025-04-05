package com.example.gkys.model.dto.responce;

import com.example.gkys.model.ItemModel;
import com.example.gkys.model.MarketModel;
import com.example.gkys.model.RoomLvlModel;

import java.util.List;

public record MarketAllDTO(List<MarketModel> marketModels, List<RoomLvlModel> roomModels, List<ItemModel> itemModels) {}
