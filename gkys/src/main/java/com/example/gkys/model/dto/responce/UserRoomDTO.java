package com.example.gkys.model.dto.responce;

import com.example.gkys.model.RoomModel;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRoomDTO {
    private RoomModel room;
    private List<String> templates;

    public UserRoomDTO (RoomModel room, List<String> templates) {
        this.room = room;
        this.templates = templates;
    }
}
