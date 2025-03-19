package com.example.gkys.controller;

import com.example.gkys.model.UserModel;
import com.example.gkys.model.dto.request.AuthenticationDTO;
import com.example.gkys.model.dto.request.RegisterDTO;
import com.example.gkys.model.dto.responce.LoginDTO;
import com.example.gkys.repository.UserRepository;
import com.example.gkys.security.TokenService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/gkys/auth", produces = {"application/json"})
public class AuthenticationController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginDTO> login(@RequestBody AuthenticationDTO data) {
        var token = tokenService.generateToken(data.login());
        return ResponseEntity.ok(new LoginDTO(token));
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> register(@RequestBody RegisterDTO data) {
        if (userRepository.findByLogin(data.login()) != null) return ResponseEntity.badRequest().build();

        String encryptedPassword = passwordEncoder.encode(data.password());
        UserModel user = new UserModel(data.login(), encryptedPassword);
        userRepository.save(user);

        return ResponseEntity.ok().build();
    }
}
