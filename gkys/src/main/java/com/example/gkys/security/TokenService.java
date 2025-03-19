package com.example.gkys.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.gkys.model.UserModel;
import com.example.gkys.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

@Service
public class TokenService {

    @Autowired
    private UserRepository userRepository;

    @Value("${api.security.token.secret}")
    private String secret;

    private Logger loger = LoggerFactory.getLogger(TokenService.class);

    public String generateToken(String login) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withSubject(login)
                    .withExpiresAt(generateExpirationDate())
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Error while generating token", exception);
        }
    }

    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            var jwtData = JWT
            .require(algorithm)
            .build()
            .verify(token)
            .getSubject();
            loger.info(jwtData);
            return jwtData;
        } catch (JWTVerificationException exception) {
            return "Не верный аунтефикатор";
        }
    }

    public UserModel getUserByJWT(String bearer) {
        if (bearer == null) return null;
        String token = bearer.replace("Bearer ", "");
        String user_login = this.validateToken(token);
        Optional<UserModel> userOptional = userRepository.findByLogin(user_login);
        if (userOptional.isPresent()) {
            UserModel userModel = userOptional.get();
            return userModel;
        } 
        else {
            throw new RuntimeException("Юзер не найден в JWT");
        }

    }

    private Instant generateExpirationDate() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.UTC);
    }
}
