package com.example.gkys.model.dto.responce;

import com.example.gkys.model.UserItemModel;

public record UserItemDTO(Iterable<UserItemModel> userItemModels) {}
