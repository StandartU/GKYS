package com.example.walkiepaws.backend;

import com.example.walkiepaws.backend.model.dto.request.AuthenticationDTO;
import com.example.walkiepaws.backend.model.dto.request.RegisterDTO;
import com.example.walkiepaws.backend.model.dto.responce.LoginDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("gkys/auth/login")
    Call<LoginDTO> auth(@Body AuthenticationDTO authenticationDTO);

    @POST("gkys/auth/register")
    Call<Void> register(@Body RegisterDTO registerDTO);


}