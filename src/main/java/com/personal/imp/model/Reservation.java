package com.personal.imp.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Date;
import java.sql.Time;

@Data
@Entity
public class Reservation {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @ManyToOne @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String theme;
    private Date date;
    private  Time startTime;
    private Time endTime;
    private String status;

}
