package com.example.gkys.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.gkys.model.RoomModel;
import com.example.gkys.model.StateModel;
import com.example.gkys.model.UserModel;
import com.example.gkys.model.UserRoomModel;
import com.example.gkys.model.UserStateModel;
import com.example.gkys.repository.RoomRepository;
import com.example.gkys.repository.StateRepository;
import com.example.gkys.repository.UserRepository;
import com.example.gkys.repository.UserRoomRepository;
import com.example.gkys.repository.UserStateRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserStateRepository userStateRepository;

    @Autowired
    private UserRoomRepository userRoomRepository;

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private RoomRepository roomRepository;

    public void setUserCash(int cash, UserModel userModel) {
        if (userModel.getCash() + cash > 0) {
            userModel.setCash(userModel.getCash() + cash);
            userRepository.save(userModel);
        }
    }

    public int getCash(UserModel userModel) {
        return userModel.getCash();
    }

    public void initUser(UserModel userModel) {
        Iterable<StateModel> stateModels = stateRepository.findAll();
        Iterable<RoomModel> roomModels = roomRepository.findAll();
        for (StateModel stateModel : stateModels) {
            UserStateModel userStateModel = new UserStateModel(userModel, stateModel, 100);
            userStateRepository.save(userStateModel);
        }
        for (RoomModel roomModel : roomModels) {
            UserRoomModel userRoomModel = new UserRoomModel(userModel, roomModel, 1);
            userRoomRepository.save(userRoomModel);
        }
    }

    

    
}
