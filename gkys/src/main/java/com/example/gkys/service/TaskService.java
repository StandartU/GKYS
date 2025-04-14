package com.example.gkys.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.gkys.model.TasksModel;
import com.example.gkys.model.UserModel;
import com.example.gkys.model.UserTaskModel;
import com.example.gkys.repository.TaskRepository;
import com.example.gkys.repository.UserTaskRepository;

@Service
@Transactional
public class TaskService {
    @Autowired
    private UserTaskRepository userTaskRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private StepsService stepsService;

    @Autowired
    private UserService userService;

    public void updateTask(String taskName, UserModel userModel) {
        initTasksForUser(userModel);
        TasksModel taskModel = taskRepository.findByName(taskName).get();
        UserTaskModel userTask = userTaskRepository.findByUserAndTask(userModel, taskModel).get();
        switch (taskModel.getName()) {
            case "feed" -> {
                userTask.setValue(userTask.getValue() + 1);
                userTaskRepository.save(userTask);
                if (userTask.getValue() + 1 == taskModel.getTarget()) {
                    userService.setUserCash(userTask.getTask().getRevard(), userModel);
                }
            }

            case "game" -> {
                userTask.setValue(userTask.getValue() + 1);
                userTaskRepository.save(userTask);
                if (userTask.getValue() + 1 == taskModel.getTarget()) {
                    userService.setUserCash(userTask.getTask().getRevard(), userModel);
                }
            }

            case "walk" -> {
                int todayDow = LocalDate.now().getDayOfWeek().getValue(); 
                List<Integer> steps = stepsService.getStepsPerDayForCurrentWeek(userModel.getId());
                int todayIndex = todayDow - 1;
                int todaySteps = steps.get(todayIndex);
                userTask.setValue(todaySteps);
                userTaskRepository.save(userTask);
                if (todaySteps == taskModel.getTarget()) {
                    userService.setUserCash(userTask.getTask().getRevard(), userModel);
                }
            }
        
            default -> {

            }
        }
    }

    public List<UserTaskModel> getTasks(UserModel userModel) {
        List<UserTaskModel> userTaskModels = new ArrayList<>();
        userTaskRepository.findAllByUser(userModel).forEach(userTask -> {userTaskModels.add(userTask);});
        return userTaskModels;
    }

    public void initTasksForUser(UserModel userModel) {
        if (!userTaskRepository.findAllByUser(userModel).iterator().hasNext()) {
            taskRepository.findAll().forEach(task -> {
                userTaskRepository.save(new UserTaskModel(task, userModel, 0));
            });
        };

    }
}
