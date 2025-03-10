package com.personal.imp.model;

import jakarta.persistence.*;
import lombok.Data;
import java.sql.Date;
import java.sql.Time;

@Data
@Entity
public class Availability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    private String theme;
    private Date date;
    private Time startTime;
    private Time endTime;
    private boolean isAvailable;
}
