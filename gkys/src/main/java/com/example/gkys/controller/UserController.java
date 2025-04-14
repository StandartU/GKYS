package com.example.gkys.controller;
import com.example.gkys.service.UserStateService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.gkys.model.UserModel;
import com.example.gkys.model.UserStateModel;
import com.example.gkys.model.dto.request.RoomLvlDTO;
import com.example.gkys.model.dto.request.UserAddCashDTO;
import com.example.gkys.model.dto.request.UserSetStateDTO;
import com.example.gkys.model.dto.responce.CashDTO;
import com.example.gkys.model.dto.responce.GetTasksDTO;
import com.example.gkys.model.dto.responce.UserRoomDTO;
import com.example.gkys.model.dto.responce.UserStateDTO;
import com.example.gkys.model.dto.responce.WeekStepsDTO;
import com.example.gkys.security.TokenService;
import com.example.gkys.service.StepsService;
import com.example.gkys.service.TaskService;
import com.example.gkys.service.UserRoomService;
import com.example.gkys.service.UserService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;




@RestController
@RequestMapping(value = "/gkys/user", produces = {"application/json"})
public class UserController {

    @Autowired
    private UserRoomService userRoomService;
    @Autowired
    private UserStateService userStateService;
    @Autowired
    private UserService userService;
    @Autowired
    private StepsService stepsService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private TaskService taskService;

    @PostMapping(value = "/add_cash", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addCash(@RequestBody UserAddCashDTO dto, @RequestHeader("Authorization") String authHeader) {
        UserModel userModel = tokenService.getUserByJWT(authHeader);
        userService.setUserCash(dto.cash(), userModel);
        if (dto.isStep()) {
            stepsService.insertSteps(dto.cash(), userModel);
            taskService.updateTask("walk", userModel);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/get_state")
    public ResponseEntity<UserStateDTO> getState(@RequestHeader("Authorization") String authHeader) {
        UserModel userModel = tokenService.getUserByJWT(authHeader);
        List<UserStateModel> userStates = userStateService.getState(userModel);
        return ResponseEntity.ok(new UserStateDTO(userStates));
    }

    @GetMapping(value = "/get_rooms")
    public ResponseEntity<List<UserRoomDTO>> getRooms(@RequestHeader("Authorization") String authHeader) {
        UserModel userModel = tokenService.getUserByJWT(authHeader);
        List<UserRoomDTO> userRoomsData = userRoomService.getRooms(userModel);
        return ResponseEntity.ok(userRoomsData);
    }

    @PostMapping(value = "/set_room_lvl", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> setRoomLvl(@RequestBody RoomLvlDTO dto, @RequestHeader("Authorization") String authHeader) {
        UserModel userModel = tokenService.getUserByJWT(authHeader);
        userRoomService.setRoomLvl(dto.lvl(), dto.name(), userModel);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/set_state", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> setState(@RequestBody UserSetStateDTO dto, @RequestHeader("Authorization") String authHeader) {
        UserModel userModel = tokenService.getUserByJWT(authHeader);
        userStateService.setState(userModel, dto.stateName(), dto.value());
        if ("happiness".equals(dto.stateName())) {
            taskService.updateTask("game", userModel);
        }
        return ResponseEntity.ok().build();
    }
    
    @GetMapping(value = "/get_cash")
    public ResponseEntity<CashDTO> getCash(@RequestHeader("Authorization") String authHeader) {
        UserModel userModel = tokenService.getUserByJWT(authHeader);
        return ResponseEntity.ok(new CashDTO(userService.getCash(userModel)));
    }

    @GetMapping(value = "/get_week_steps")
    public ResponseEntity<WeekStepsDTO> getWeekSteps(@RequestHeader("Authorization") String authHeader) {
        UserModel userModel = tokenService.getUserByJWT(authHeader);
        return ResponseEntity.ok(new WeekStepsDTO(stepsService.getStepsPerDayForCurrentWeek(userModel.getId())));
    }

    @GetMapping(value = "/get_tasks")
    public ResponseEntity<GetTasksDTO> getTasks(@RequestHeader("Authorization") String authHeader) {
        UserModel userModel = tokenService.getUserByJWT(authHeader);
        taskService.initTasksForUser(userModel);
        return ResponseEntity.ok(new GetTasksDTO(taskService.getTasks(userModel)));
    }
}
