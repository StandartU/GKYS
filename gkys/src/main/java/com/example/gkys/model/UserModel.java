package com.example.gkys.model;

import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "user")
public class UserModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String login;

    @Column(nullable = false)
    private String password;

    private int cash;

    public UserModel(String login, String password) {
        this.login = login;
        this.password = password;
    }

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<StepsModel> steps;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserItemModel> userItems;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserRoomModel> userRooms;

    
}
