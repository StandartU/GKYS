package com.example.walkiepaws.backend.model.dto.responce;


import com.example.walkiepaws.backend.model.RoomModel;

import java.util.List;


public record UserRoomDTO(RoomModel room, List<String> templates){}
