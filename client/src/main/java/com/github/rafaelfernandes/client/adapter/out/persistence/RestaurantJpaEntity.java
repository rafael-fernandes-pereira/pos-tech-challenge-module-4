package com.github.rafaelfernandes.client.adapter.out.persistence;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "restaurant",
        indexes = {
            @Index(name = "idx_full_search", columnList = "full_search")
        }
    )
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantJpaEntity {

    @Id
    private UUID id;

    @Column(name = "name", unique = true)
    private String name;

    @Column(name = "register")
    private LocalDateTime register;

    @Column(name = "tables")
    private Integer tables;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "restaurant")
    private List<OpeningHourJpaEntity> openingHours;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "restaurant")
    private List<CuisineJpaEntity> cuisines;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private AddressJpaEntity address;

    @Column(name = "full_search", columnDefinition = "TEXT")
    private String fullSearch;




}
