package com.example.walkiepaws.backend;

import com.example.walkiepaws.backend.model.dto.request.AuthenticationDTO;
import com.example.walkiepaws.backend.model.dto.request.BuyItemDTO;
import com.example.walkiepaws.backend.model.dto.request.BuyRoomLvlDTO;
import com.example.walkiepaws.backend.model.dto.request.GetPetDTO;
import com.example.walkiepaws.backend.model.dto.request.ItemActive;
import com.example.walkiepaws.backend.model.dto.request.MarketBuyDTO;
import com.example.walkiepaws.backend.model.dto.request.RegisterDTO;
import com.example.walkiepaws.backend.model.dto.request.RoomLvlDTO;
import com.example.walkiepaws.backend.model.dto.request.StepCountDTO;
import com.example.walkiepaws.backend.model.dto.request.UserAddCashDTO;
import com.example.walkiepaws.backend.model.dto.request.UserSetStateDTO;
import com.example.walkiepaws.backend.model.dto.responce.LoginDTO;
import com.example.walkiepaws.backend.model.dto.responce.MarketAllDTO;
import com.example.walkiepaws.backend.model.dto.responce.PetDTO;
import com.example.walkiepaws.backend.model.dto.responce.UserItemDTO;
import com.example.walkiepaws.backend.model.dto.responce.UserRoomDTO;
import com.example.walkiepaws.backend.model.dto.responce.UserStateDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {

    String abs_path = "gkys/";

    // AUTH
    @POST(abs_path + "auth/login")
    @Headers("Content-Type: application/json")
    Call<LoginDTO> auth(@Body AuthenticationDTO dto);

    @POST(abs_path + "auth/register")
    @Headers("Content-Type: application/json")
    Call<Void> register(@Body RegisterDTO dto);

    //ITEM CONTROLLER
    @GET(abs_path + "item/get_items")
    Call<UserItemDTO> getUserItems(@Header("Authorization") String token);

    @POST(abs_path + "item/set_item_active")
    @Headers("Content-Type: application/json")
    Call<Void> setItemActive(@Header("Authorization") String token, @Body ItemActive dto);

    // MARKET CONTROLLER
    @POST(abs_path + "market/buy_state")
    @Headers("Content-Type: application/json")
    Call<Void> buyState(@Header("Authorization") String token, @Body MarketBuyDTO dto);

    @POST(abs_path + "market/buy_room")
    @Headers("Content-Type: application/json")
    Call<Void> buyRoom(@Header("Authorization") String token, @Body BuyRoomLvlDTO dto);

    @POST(abs_path + "market/buy_item")
    @Headers("Content-Type: application/json")
    Call<Void> buyItem(@Header("Authorization") String token, @Body BuyItemDTO dto);

    @GET(abs_path + "market/all_buyers")
    Call<MarketAllDTO> getAllMarkets(@Header("Authorization") String token);

    // PET CONTROLLER
    @POST(abs_path + "pet/get_pet")
    @Headers("Content-Type: application/json")
    Call<PetDTO> getPet(@Header("Authorization") String token, @Body GetPetDTO dto);

    // USER CONTROLLER

    @POST(abs_path + "user/add_cash")
    @Headers("Content-Type: application/json")
    Call<Void> addCash(@Header("Authorization") String token, @Body UserAddCashDTO dto);

    @POST(abs_path + "user/insert_steps")
    @Headers("Content-Type: application/json")
    Call<Void> insertSteps(@Header("Authorization") String token, @Body StepCountDTO dto);

    @GET(abs_path + "user/get_state")
    Call<UserStateDTO> getState(@Header("Authorization") String token);

    @GET(abs_path + "user/get_rooms")
    Call<List<UserRoomDTO>> getRooms(@Header("Authorization") String token);

    @POST(abs_path + "user/set_room_lvl")
    @Headers("Content-Type: application/json")
    Call<Void> setRoomLvl(@Header("Authorization") String token, @Body RoomLvlDTO dto);

    @POST(abs_path + "user/set_state")
    @Headers("Content-Type: application/json")
    Call<Void> setState(@Header("Authorization") String token, @Body UserSetStateDTO dto);

}