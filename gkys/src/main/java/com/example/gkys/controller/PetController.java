package com.example.gkys.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.gkys.model.UserModel;
import com.example.gkys.model.dto.request.GetPetDTO;
import com.example.gkys.model.dto.responce.PetDTO;
import com.example.gkys.security.TokenService;
import com.example.gkys.service.PetService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;


@RestController
@RequestMapping(value = "/gkys/pet", produces = {"application/json"})
public class PetController {
    @Autowired
    private TokenService tokenService;

    @Autowired
    private PetService petService;

    @PostMapping(value = "/get_pet", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PetDTO> getPet(@RequestBody GetPetDTO dto, @RequestHeader("Authorization") String authHeader) {
        UserModel userModel = tokenService.getUserByJWT(authHeader);
        return ResponseEntity.ok(new PetDTO(petService.getPet(dto.name(), userModel)));
    }
    
}
