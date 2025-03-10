package com.personal.imp.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class BlockedUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private User blockedUser;
}
