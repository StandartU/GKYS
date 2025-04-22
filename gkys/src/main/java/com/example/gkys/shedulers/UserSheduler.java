package com.example.gkys.shedulers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.gkys.model.TasksModel;
import com.example.gkys.model.UserModel;
import com.example.gkys.model.UserTaskModel;
import com.example.gkys.repository.TaskRepository;
import com.example.gkys.repository.UserRepository;
import com.example.gkys.repository.UserTaskRepository;
import com.example.gkys.service.UserStateService;

@Component
public class UserSheduler {

    @Autowired
    private UserStateService userStateService;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserTaskRepository userTaskRepository;

    @Autowired
    private UserRepository userRepository;

    @Scheduled(fixedRate = 360_000)
    public void decreaseHunger() {userStateService.decreaseState("hunger");}
    @Scheduled(fixedRate = 360_000)
    public void decreaseSleep() {userStateService.decreaseState("sleep");}
    @Scheduled(fixedRate = 360_000)
    public void decreaseHappiness() {userStateService.decreaseState("happiness");}
    
    @Scheduled(fixedRate = 3_600_000)
    public void checkUserPetState() {
        Iterable<UserModel> users = userRepository.findAll();
        TasksModel task = taskRepository.findByName("return").get();
        users.forEach(user -> {
            if (!userTaskRepository.findByUserAndTask(user, task).isPresent()) {
                userTaskRepository.save(new UserTaskModel(task, user, 0));
            }
        });
        userTaskRepository.findAllByTask(task).forEach(userTask -> {
            if (userStateService.isStateIsZero(userTask.getUser())) {
                userTask.setValue(userTask.getValue() + 1);
            } else {
                userTask.setValue(0);
            }
            userTaskRepository.save(userTask);
        });
    }
}
