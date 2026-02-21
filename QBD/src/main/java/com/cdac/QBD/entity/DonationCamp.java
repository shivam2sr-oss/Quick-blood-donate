package com.cdac.QBD.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "donation_camps")
public class DonationCamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Which Node is organizing this camp?
    @ManyToOne
    @JoinColumn(name = "org_id", nullable = false)
    private Organization organization;

    private String campName; // e.g., "Symbiosis College Drive"

    private String address;

    private LocalDate campDate;

    private String startTime;
    private String endTime;

    // To track success of the camp
    private int projectedUnits;
    private int actualUnitsCollected;
}
