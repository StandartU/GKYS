package com.example.gkys.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.gkys.model.UserModel;
import com.example.gkys.model.dto.request.ItemActive;
import com.example.gkys.model.dto.responce.UserItemDTO;
import com.example.gkys.security.TokenService;
import com.example.gkys.service.ItemService;

@RestController
@RequestMapping(value = "/gkys/item", produces = {"application/json"})
public class ItemController {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ItemService itemService;

    @GetMapping("/get_items")
    public ResponseEntity<UserItemDTO> getUserItems(@RequestHeader("Authorization") String authHeader) {
        UserModel userModel = tokenService.getUserByJWT(authHeader);
        return ResponseEntity.ok(new UserItemDTO(itemService.getUserItems(userModel)));
    }
    
    @PostMapping("/set_item_active")
    public ResponseEntity<?> postMethodName(@RequestBody ItemActive dto) {
        itemService.setItemActive(dto.id(), dto.active());
        return ResponseEntity.ok().build();
    }
}
