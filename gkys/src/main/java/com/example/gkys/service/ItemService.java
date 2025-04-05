package com.example.gkys.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.gkys.model.UserItemModel;
import com.example.gkys.model.UserModel;
import com.example.gkys.repository.UserItemRepository;

import jakarta.transaction.Transactional;


@Service
@Transactional
public class ItemService {
    @Autowired
    private UserStateService userStateService;

    @Autowired
    private UserItemRepository userItemRepository;

    public List<UserItemModel> getUserItems(UserModel userModel) {
        Iterable<UserItemModel> userIterable = userItemRepository.findAllByUserAndLvl(userModel, userStateService.getUserStateLvl(userModel));
        List<UserItemModel> answer = new ArrayList<>();
        userIterable.forEach(answer::add);
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
}
