package com.personal.imp.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private String email;
    private String phoneNumber;
    private String profilePictureUrl;
    private Date dateOfBirth;

    @Enumerated(EnumType.STRING)
    private Gender gender = Gender.OTHER;
    private String address;
    private String zipCode;

    @Column(columnDefinition = "JSON")
    private String socialMediaAccounts;
    private Date registrationDate;
    private Date lastLogin;

    @Column(columnDefinition = "JSON")
    private String settings;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @Transient
    private boolean isLocalAccount = false;

    public enum Gender {
        MALE, FEMALE, OTHER
    }
}
