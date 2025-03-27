package com.example.walkiepaws.backend.model.dto.responce;


import com.example.walkiepaws.backend.model.UserItemModel;

public record UserItemDTO(Iterable<UserItemModel> userItemModels) {}
