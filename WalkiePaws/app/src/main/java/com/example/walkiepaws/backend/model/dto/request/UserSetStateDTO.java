package com.example.walkiepaws.backend.model.dto.request;


import com.example.walkiepaws.backend.model.UserStateModel;

public record UserSetStateDTO(String stateName, int value) {}