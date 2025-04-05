package com.example.gkys.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_room")
@Setter
@Getter
@NoArgsConstructor
public class UserRoomModel {

    public UserRoomModel(UserModel user, RoomModel room, int lvl) {
        this.user = user;
        this.room = room;
        this.lvl = lvl;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserModel user;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private RoomModel room;

    private int lvl;
}