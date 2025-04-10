package com.example.walkiepaws.backend.model.dto.responce;

import com.example.walkiepaws.backend.model.UserTaskModel;

import java.util.List;

public record GetTasksDTO(List<UserTaskModel> userTasks) {}
