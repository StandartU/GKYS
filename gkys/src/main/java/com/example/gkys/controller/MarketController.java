package com.example.gkys.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.gkys.model.ItemModel;
import com.example.gkys.model.MarketModel;
import com.example.gkys.model.RoomLvlModel;
import com.example.gkys.model.UserModel;
import com.example.gkys.model.dto.request.BuyItemDTO;
import com.example.gkys.model.dto.request.BuyRoomLvlDTO;
import com.example.gkys.model.dto.request.MarketBuyDTO;
import com.example.gkys.model.dto.responce.MarketAllDTO;
import com.example.gkys.security.TokenService;
import com.example.gkys.service.MarketService;
import com.example.gkys.service.TaskService;

import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping(value = "/gkys/market", produces = {"application/json"})
public class MarketController {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private MarketService marketService;

    @Autowired
    private TaskService taskService;

    @PostMapping(value = "/buy_state", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> buyState (@RequestBody MarketBuyDTO marketBuyDTO, @RequestHeader("Authorization") String authHeader) {
        UserModel userModel = tokenService.getUserByJWT(authHeader);
        int marketId = marketBuyDTO.marketId();
        marketService.buyState(marketId, userModel.getId());
        taskService.updateTask("feed", userModel);
        return ResponseEntity.ok().body("Покупка выполнена успешно!");
    }
    
    @PostMapping(value = "/buy_room", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> buyRoom(@RequestBody BuyRoomLvlDTO dto, @RequestHeader("Authorization") String authHeader) {
        UserModel userModel = tokenService.getUserByJWT(authHeader);
        marketService.buyRoom(dto.name(), dto.lvl(), userModel);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/buy_item", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> buyItem(@RequestBody BuyItemDTO dto, @RequestHeader("Authorization") String authHeader) {
        UserModel userModel = tokenService.getUserByJWT(authHeader);
        marketService.buyItem(dto.id(), userModel);
        return ResponseEntity.ok().build();
    }
    
    @SuppressWarnings("unchecked")
    @GetMapping(value = "/all_buyers")
    public ResponseEntity<MarketAllDTO> getAllMarkets(@RequestHeader("Authorization") String authHeader) {
        UserModel userModel = tokenService.getUserByJWT(authHeader);
        List<List<?>> marketRoomItem = marketService.getAllMarkets(userModel);
        return ResponseEntity.ok(new MarketAllDTO(
            (List<MarketModel>) marketRoomItem.get(0),
            (List<RoomLvlModel>) marketRoomItem.get(1),
            (List<ItemModel>) marketRoomItem.get(2)));
    }
}