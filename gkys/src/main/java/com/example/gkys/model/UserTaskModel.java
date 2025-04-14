package com.example.gkys.model;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity(name = "user_task")
@NoArgsConstructor
public class UserTaskModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private TasksModel task;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserModel user;

    private int value;

    private Date date;

    public UserTaskModel(TasksModel task, UserModel user, int value) {
        this.task = task;
        this.user = user;
        this.value = value;
    }

    @PrePersist
    protected void onCreate() {
        if (date == null) {
            date = new Date();
        }
    }
}
