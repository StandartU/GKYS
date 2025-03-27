package com.example.gkys.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.gkys.model.PetModel;
import com.example.gkys.model.PetStateModel;
import com.example.gkys.model.UserModel;
import com.example.gkys.repository.PetRepository;
import com.example.gkys.repository.PetStateRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class PetService {
    @Autowired
    private PetRepository petRepository;

    @Autowired
    private PetStateRepository petStateRepository;

    @Autowired
    private UserStateService userStateService;

    public PetStateModel getPet(String name, UserModel userModel) {
        Optional<PetModel> petOptional = petRepository.findByName(name);
        if (!petOptional.isPresent()) {
            throw new RuntimeException("Не найден пет");
        }
        PetModel petModel = petOptional.get();
        int lvl = userStateService.getUserStateLvl(userModel);      
        Optional<PetStateModel> petStateOptional = petStateRepository.findByPetAndLvl(petModel, lvl);
        if (!petStateOptional.isPresent()) {
            throw new RuntimeException("Не найденно данных по лвлу пета");
        }
        return petStateOptional.get();
    }
}