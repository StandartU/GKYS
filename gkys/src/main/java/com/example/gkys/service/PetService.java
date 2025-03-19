package com.example.gkys.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.gkys.model.PetModel;
import com.example.gkys.model.PetStateModel;
import com.example.gkys.model.UserModel;
import com.example.gkys.model.UserStateModel;
import com.example.gkys.repository.PetRepository;
import com.example.gkys.repository.PetStateRepository;
import com.example.gkys.repository.UserStateRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class PetService {
    @Autowired
    private PetRepository petRepository;

    @Autowired
    private PetStateRepository petStateRepository;

    @Autowired
    private UserStateRepository userStateRepository;

    public PetStateModel getPet(String name, UserModel userModel) {
        Optional<PetModel> petOptional = petRepository.findByName(name);
        if (!petOptional.isPresent()) {
            throw new RuntimeException("Не найден пет");
        }
        PetModel petModel = petOptional.get();
        Iterable<UserStateModel> userStateModels = userStateRepository.findAllByUser(userModel);
        int value = 0;
        for (UserStateModel userStateModel : userStateModels) {
            value += userStateModel.getValue();
        }
        value /= 3;
        if (value > 70) value = 1;
        else if (value > 30) value = 2;
        else if (value > 0) value = 3;        
        Optional<PetStateModel> petStateOptional = petStateRepository.findByPetAndLvl(petModel, value);
        if (!petStateOptional.isPresent()) {
            throw new RuntimeException("Не найденно данных по лвлу пета");
        }
        return petStateOptional.get();
    }
}