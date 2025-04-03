package com.example.gkys.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.gkys.model.MarketModel;
import com.example.gkys.model.StateModel;
import com.example.gkys.model.UserModel;
import com.example.gkys.model.UserStateModel;
import com.example.gkys.repository.UserStateRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserStateService {
    @Autowired
    private UserStateRepository userStateRepository;

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

    public Iterable<UserStateModel> getState (UserModel userModel) {
        Iterable<UserStateModel> userStates =  userStateRepository.findAllByUser(userModel);
        return userStates;
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
        else if (value > 0) value = 3; 
        return value;
    }

    public void setState(UserStateModel userStateModel, int value) {
        userStateModel.setValue(userStateModel.getValue() + value);
        userStateRepository.save(userStateModel);
    }
}
