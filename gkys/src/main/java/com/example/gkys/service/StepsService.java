package com.example.gkys.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.gkys.model.StepsModel;
import com.example.gkys.model.UserModel;
import com.example.gkys.repository.StepsRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class StepsService {
    @Autowired
    private StepsRepository stepsRepository;

    public void insertSteps(int steps, UserModel userModel) {
        StepsModel stepsModel = new StepsModel(userModel, steps);
        stepsRepository.save(stepsModel);
    }
}
