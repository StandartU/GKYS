package com.example.gkys.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.gkys.model.StepsModel;
import com.example.gkys.model.UserModel;
import com.example.gkys.repository.StepsRepository;


@Service
@Transactional
public class StepsService {
    @Autowired
    private StepsRepository stepsRepository;

    public void insertSteps(int steps, UserModel userModel) {
        StepsModel stepsModel = new StepsModel(userModel, steps);
        stepsRepository.save(stepsModel);
    }


    public List<Integer> getStepsPerDayForCurrentWeek(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(DayOfWeek.MONDAY);
        LocalDate sunday = today.with(DayOfWeek.SUNDAY);

        Date startOfWeek = java.sql.Date.valueOf(monday);
        Date endOfWeek = java.sql.Date.valueOf(sunday);

        List<Object[]> rawSteps = stepsRepository.findStepsPerDayOfWeek(userId, startOfWeek, endOfWeek);

        Map<Integer, Integer> stepsByDayOfWeek = new HashMap<>();
        for (int i = 1; i <= 7; i++) {
            stepsByDayOfWeek.put(i, 0);
        }

        for (Object[] row : rawSteps) {
            Integer dayOfWeek = ((Number) row[0]).intValue();
            Integer steps = ((Number) row[1]).intValue();
            stepsByDayOfWeek.put(dayOfWeek, steps);
        }

        List<Integer> result = new ArrayList<>();
        for (int i = 2; i <= 8; i++) {
            int dow = (i == 8) ? 1 : i;
            result.add(stepsByDayOfWeek.get(dow));
        }

        return result;
    }

}
