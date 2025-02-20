package com.example.gkys.model;
import java.util.Date;
import jakarta.persistence.*;

@Entity
@Table(name = "steps")
public class Steps {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userId;
    
    @Column(columnDefinition = "DATE DEFAULT CURRENT_DATE")
    private Date date;

    private int steps;

}
