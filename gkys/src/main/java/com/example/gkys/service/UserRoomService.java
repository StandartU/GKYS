package com.example.gkys.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.gkys.model.RoomLvlModel;
import com.example.gkys.model.RoomModel;
import com.example.gkys.model.UserModel;
import com.example.gkys.model.UserRoomModel;
import com.example.gkys.model.dto.responce.UserRoomDTO;
import com.example.gkys.repository.RoomLvlRepository;
import com.example.gkys.repository.RoomRepository;
import com.example.gkys.repository.UserRoomRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserRoomService {

    @Autowired
    private UserRoomRepository userRoomRepository;

    @Autowired
    private RoomLvlRepository roomLvlRepository;

    @Autowired
    private RoomRepository roomRepository;

    public List<UserRoomDTO> getRooms(UserModel userModel) {
        Iterable<UserRoomModel> userRooms = userRoomRepository.findAllByUser(userModel);
        List<UserRoomDTO> userRoomsDTO = new ArrayList<UserRoomDTO>();
        for (UserRoomModel userRoom : userRooms) {
            int lvl = userRoom.getLvl();
            Optional<RoomLvlModel> roomOptional = roomLvlRepository.findByRoomAndLvl(userRoom.getRoom(), lvl);
            if (!roomOptional.isPresent()) {
                throw new RuntimeException("Не найдены данные о комнате");
            }
            RoomLvlModel roomData = roomOptional.get();
            userRoomsDTO.add(new UserRoomDTO(roomData.getRoom(), roomData.getTemplates()));
        }
        return userRoomsDTO;
    }

    public void setRoomLvl(int lvl, String roomName, UserModel userModel) {
        Optional<RoomModel> roomOptional = roomRepository.findByName(roomName);
        if (!roomOptional.isPresent()) {
            throw new RuntimeException("Такой комнаты нет");
        }
        Optional<UserRoomModel> userRoomOptional = userRoomRepository.findByUserAndRoom(userModel, roomOptional.get());
        if (!userRoomOptional.isPresent()) {
            throw new RuntimeException("Такой комнаты юзера не существует");
        }
        UserRoomModel userRoomModel = userRoomOptional.get();
        userRoomModel.setLvl(lvl);
        userRoomRepository.save(userRoomModel);
    }
}
