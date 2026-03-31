package com.quickfare.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Fare entity — stores user-contributed fare data for analytics and comparison.
 */
@Entity
@Table(name = "fares")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Fare {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ServiceProvider service;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RideType rideType;

    // ---- Pickup Location ----
    private String pickupName;
    private Double pickupLatitude;
    private Double pickupLongitude;

    // ---- Drop Location ----
    private String dropName;
    private Double dropLatitude;
    private Double dropLongitude;

    @Column(nullable = false)
    private Double actualFare;

    @Column(nullable = false)
    private Double distance;

    @Column(nullable = false)
    private Double duration;

    @Builder.Default
    private Double surgeMultiplier = 1.0;

    private String notes;

    @Builder.Default
    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    // ---- Enums ----

    public enum ServiceProvider {
        UBER, OLA, RAPIDO
    }

    public enum RideType {
        BIKE, AUTO, CAR, SUV, PREMIUM, MINI, SEDAN, GO, PREMIER
    }
}
