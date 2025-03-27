package com.example.gkys.service;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.gkys.repository.*;
import jakarta.transaction.Transactional;
import com.example.gkys.model.ItemModel;
import com.example.gkys.model.MarketModel;
import com.example.gkys.model.RoomLvlModel;
import com.example.gkys.model.RoomModel;
import com.example.gkys.model.UserItemModel;
import com.example.gkys.model.UserModel;
import com.example.gkys.model.UserRoomModel;

@Service
@Transactional
public class MarketService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private MarketRepository marketRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserStateService userStateService;

    @Autowired
    private RoomLvlRepository roomLvlRepository;

    @Autowired
    private UserRoomRepository userRoomRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserItemRepository userItemRepository;

    public List<Iterable<?>> getAllMarkets(UserModel userModel) {
        Iterable<MarketModel> marketModels = marketRepository.findAll();
        Iterable<UserRoomModel> userRoomModels = userRoomRepository.findAllByUser(userModel);
        List<RoomLvlModel> roomLvlModelsList = new ArrayList<RoomLvlModel>();
        for (UserRoomModel userRoomModel : userRoomModels) {
            Optional<RoomLvlModel> roomLvlOptional = roomLvlRepository.findByRoomAndLvl(userRoomModel.getRoom(), userRoomModel.getLvl() + 1);
            if (!roomLvlOptional.isPresent()) {
                throw new RuntimeException("Не найдена комната по лвлу");
            }
            RoomLvlModel roomLvlModel = roomLvlOptional.get();
            roomLvlModelsList.add(roomLvlModel);
        }
        Iterable<RoomLvlModel> roomLvlModels = roomLvlModelsList;
        Iterable<ItemModel> itemModels = itemRepository.findAll();
        List<UserItemModel> userItemModels = userModel.getUserItems();
        List<Integer> userItemIds = userItemModels.stream()
                                    .map(userItem -> userItem.getItem().getId())
                                    .collect(Collectors.toList());
        List<ItemModel> itemsNotOwnedList = StreamSupport.stream(itemModels.spliterator(), false)
                                                .filter(item -> !userItemIds.contains(item.getId()))
                                                .collect(Collectors.toList());
        Iterable<ItemModel> itemModelsNotOwned = itemsNotOwnedList;     
        return List.of(marketModels, roomLvlModels, itemModelsNotOwned);
    }

    public void buyState(int marketId, Long userId) {
        Optional<UserModel> userOptional = userRepository.findById(userId);
        Optional<MarketModel> marketOptional = marketRepository.findById(marketId);

        if (!userOptional.isPresent() || !marketOptional.isPresent()) {
            throw new RuntimeException("Пользователь или рынок не найдены");
        }

        UserModel user = userOptional.get();
        MarketModel market = marketOptional.get();

        if (user.getCash() < market.getValue()) {
            throw new RuntimeException("Недостаточно средств");
        }

        userStateService.setStateUser(user, market.getState(), market);
        userService.setUserCash(-market.getValue(), user);
    }

    public void buyItem(int id, UserModel userModel) {
        Optional<ItemModel> itemOptional = itemRepository.findById(id);
        if (!itemOptional.isPresent()) {
            throw new RuntimeException("Нет айтема по заданному айди");
        }
        ItemModel itemModel = itemOptional.get();
        if (userModel.getCash() - itemModel.getPrice() > 0) {
            userModel.setCash(userModel.getCash() - itemModel.getPrice());
            UserItemModel userItemModel = new UserItemModel(itemModel, userModel);
            userItemRepository.save(userItemModel);
            userRepository.save(userModel);
        }
        else {
            throw new RuntimeException("Не достаточно средств");
        }
    }

    public void buyRoom(String name, int lvl, UserModel userModel) {
        Optional<RoomModel> roomOptional =  roomRepository.findByName(name);
        if (!roomOptional.isPresent()) {
            throw new RuntimeException("Не найдена конмата по имени");
        }
        RoomModel roomModel = roomOptional.get();

        Optional<RoomLvlModel> roomLvlOptional = roomLvlRepository.findByRoomAndLvl(roomModel, lvl);

        if (!roomLvlOptional.isPresent()) {
            throw new RuntimeException("Не найден лвл комнаты");
        }

        RoomLvlModel roomLvlModel = roomLvlOptional.get();
        
        Optional<UserRoomModel> userRoomOptional = userRoomRepository.findByUserAndRoom(userModel, roomModel);
        if (!userRoomOptional.isPresent()) {
            throw new RuntimeException("Не найденна комната юзера");
        }
        UserRoomModel userRoomModel = userRoomOptional.get();
        if (userModel.getCash() - roomLvlModel.getPrice() > 0) {
            userRoomModel.setLvl(lvl);
            userRoomRepository.save(userRoomModel);
        }
        else {
            throw new RuntimeException("Недостаточно средств");
        }
    }
}
