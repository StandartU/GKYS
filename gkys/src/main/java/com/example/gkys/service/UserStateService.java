package com.example.gkys.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.gkys.model.MarketModel;
import com.example.gkys.model.StateModel;
import com.example.gkys.model.UserModel;
import com.example.gkys.model.UserStateModel;
import com.example.gkys.repository.StateRepository;
import com.example.gkys.repository.UserStateRepository;


@Service
@Transactional
public class UserStateService {
    @Autowired
    private UserStateRepository userStateRepository;

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private TaskService taskService;

    public void setStateUserByMarket(UserModel userModel, StateModel stateModel, MarketModel marketModel) {
        Optional<UserStateModel> userStateOptional = userStateRepository.findByUserAndState(userModel, stateModel);

        if (!userStateOptional.isPresent()) {
            throw new RuntimeException("Ошибка, данных не существует");
        }

        UserStateModel userStateModel = userStateOptional.get();
        int value = userStateModel.getValue() + marketModel.getValue(); 
        if (value > 100) { value = 100; }
        userStateModel.setValue(value);
        userStateRepository.save(userStateModel);
    }

    public List<UserStateModel> getState (UserModel userModel) {
        Iterable<UserStateModel> userStates =  userStateRepository.findAllByUser(userModel);
        List<UserStateModel> userStateModelList = new ArrayList<>();
        userStates.forEach(userStateModelList::add);
        return userStateModelList;
    }

    public int getUserStateLvl(UserModel userModel) {
        Iterable<UserStateModel> userStateModels = userStateRepository.findAllByUser(userModel);
        int value = 0;
        for (UserStateModel userStateModel : userStateModels) {
            value += userStateModel.getValue();
        }
        value /= 3;
        if (value > 70) value = 1;
        else if (value > 30) value = 2;
        else if (value >= 0) value = 3; 
        return value;
    }

    public void setState(UserModel userModel, String stateName, int value) {
        UserStateModel userStateModel = userStateRepository.findByUserAndState(userModel, stateRepository.findByName(stateName).get()).get();
        userStateModel.setValue(userStateModel.getValue() + value <= 101 ? userStateModel.getValue() + value : 100 );
        userStateRepository.save(userStateModel);
        if (userStateModel.getState().getName() == "happiness") {
            taskService.updateTask("game", userModel);
        }
    }

    public void decreaseState(String stateName) {
        stateRepository.findByName(stateName).ifPresent(state -> {
            userStateRepository.findAllByState(state).forEach(userState -> {
                userState.setValue(userState.getValue() > 0 ? userState.getValue() - 1 : 0);
                userStateRepository.save(userState);
            });
        });
    }

    public boolean isStateIsZero(UserModel userModel) {
        Iterable<UserStateModel> userStates = userStateRepository.findAllByUser(userModel);
        for (UserStateModel userState: userStates) {
            if (userState.getValue() != 0) {
                return false;
            }
        }
        return true;
    }

}
