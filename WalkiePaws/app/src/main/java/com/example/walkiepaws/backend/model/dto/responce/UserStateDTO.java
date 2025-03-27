package com.example.walkiepaws.backend.model.dto.responce;


import com.example.walkiepaws.backend.model.UserStateModel;

public record UserStateDTO(Iterable<UserStateModel> userStates) {}
