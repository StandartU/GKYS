package com.example.gkys.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.gkys.model.ItemModel;
import com.example.gkys.model.PetItemModel;
import com.example.gkys.model.UserItemModel;
import com.example.gkys.model.UserModel;
import com.example.gkys.repository.ItemRepository;
import com.example.gkys.repository.PetItemRepository;
import com.example.gkys.repository.UserItemRepository;



@Service
@Transactional
public class ItemService {
    @Autowired
    private UserStateService userStateService;

    @Autowired
    private UserItemRepository userItemRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private PetItemRepository petItemRepository;

    public List<UserItemModel> getUserItems(UserModel userModel) {
        Iterable<UserItemModel> userIterable = userItemRepository.findAllByUser(userModel);
        List<UserItemModel> answer = new ArrayList<>();
        userIterable.forEach(answer::add);
        return answer;
    }

    public List<ItemModel> getItems() {
        Iterable<ItemModel> answerIterable = itemRepository.findAll();
        List<ItemModel> answer = new ArrayList<>();
        answerIterable.forEach(answer::add);
        return answer;
    }

    public void setItemActive(int id, boolean active) {
        Optional<UserItemModel> userItemOptional = userItemRepository.findById(id);
        if (!userItemOptional.isPresent()) {
            throw new RuntimeException("Не найден айтем юзера");
        }
        UserItemModel userItemModel = userItemOptional.get();
        userItemModel.setActive(active);
        userItemRepository.save(userItemModel);
    }

    public List<PetItemModel> getPetItems() {
        List<PetItemModel> answer = new ArrayList<>();
        petItemRepository.findAll().forEach(answer::add);
        return answer;
    }
}
