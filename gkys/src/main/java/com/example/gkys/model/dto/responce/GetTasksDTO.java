package com.example.gkys.model.dto.responce;

import java.util.List;

import com.example.gkys.model.UserTaskModel;

public record GetTasksDTO(List<UserTaskModel> userTasks) {}
