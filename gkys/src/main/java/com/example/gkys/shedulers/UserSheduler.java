package com.example.gkys.shedulers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.gkys.service.UserStateService;

@Component
public class UserSheduler {

    @Autowired
    private UserStateService userStateService;

    @Scheduled(fixedRate = 180_000)
    public void decreaseHunger() {userStateService.decreaseState("hunger");}
    @Scheduled(fixedRate = 300_000)
    public void decreaseSleep() {userStateService.decreaseState("sleep");}
    @Scheduled(fixedRate = 240_000)
    public void decreaseHappiness() {userStateService.decreaseState("happiness");}
}
