package com.example.gkys.model;
import java.util.Date;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "steps")
@Setter
@Getter
public class StepsModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserModel user;
    
    @Column(columnDefinition = "DATE DEFAULT CURRENT_DATE")
    private Date date;

    private int steps;

    public StepsModel(UserModel userModel, int steps) {
        this.user = userModel;
        this.steps = steps;
    }

    @PrePersist
    protected void onCreate() {
        if (date == null) {
            date = new Date();
        }
    }
}
