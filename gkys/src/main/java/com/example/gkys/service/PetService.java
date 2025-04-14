package com.example.gkys.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.gkys.model.PetModel;
import com.example.gkys.model.PetStateModel;
import com.example.gkys.model.UserModel;
import com.example.gkys.repository.PetRepository;
import com.example.gkys.repository.PetStateRepository;

@Service
@Transactional
public class PetService {
    @Autowired
    private PetRepository petRepository;

    @Autowired
    private PetStateRepository petStateRepository;

    @Autowired
    private UserStateService userStateService;

    private Logger logger = LoggerFactory.getLogger(PetService.class);

    public String getPet(String name, UserModel userModel) {
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
        PetStateModel petState = petStateOptional.get();
        logger.info("FIND THIS" + petState.getTemplate().get(0).getStateName());
        return petState.getTemplate().get(0).getStateName();
    }
}