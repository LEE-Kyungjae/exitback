package com.personal.imp.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Data
@Entity
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String location;
    private String description;
    private String contact_url;
    private String website_url;

    @OneToMany(mappedBy = "store")
    private Set<Availability> availabilities;

    @OneToMany(mappedBy = "store")
    private Set<Reservation> reservations;

    @OneToMany(mappedBy = "store")
    private Set<StoreHours> storeHours;

}
