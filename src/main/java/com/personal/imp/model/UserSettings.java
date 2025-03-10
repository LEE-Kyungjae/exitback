package com.personal.imp.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class UserSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private User user;

    private boolean notificationsEnabled;
    private String notificationTone;
}
