package com.example.gkys.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.gkys.model.UserModel;
import com.example.gkys.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void setUserCash(int cash, UserModel userModel) {
        if (userModel.getCash() + cash > 0) {
            userModel.setCash(userModel.getCash() + cash);
            userRepository.save(userModel);
        }
    }

    public void initUser(UserModel userModel) {
        
    }

    

    
}
