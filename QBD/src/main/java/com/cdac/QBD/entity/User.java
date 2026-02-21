package com.cdac.QBD.entity;

import com.cdac.QBD.utils.constant.BloodGroup;
import com.cdac.QBD.utils.constant.UserRole;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String fullName;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @ManyToOne
    @JoinColumn(name = "org_id")
    private Organization organization;

    // --- Donor Specific Fields ---
    private String aadharNumber;

    @Enumerated(EnumType.STRING)
    private BloodGroup bloodGroup;

    private LocalDate dob;
    private String gender;
    private Double weight;
    private LocalDate lastDonationDate;
    private String address;
    private String contactNumber;

    private String district;
    private String city;

    // OneToMany Relationship to MedicalHistory Entity
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MedicalHistory> medicalHistoryList = new ArrayList<>();

    public boolean isEligible() {
        // 1. New User (No history) -> ALWAYS ELIGIBLE
        if (this.lastDonationDate == null) {
            return true;
        }

        // 2. Existing Donor -> Check if 90 days have passed
        // Logic: Is the last donation BEFORE (Today - 90 Days)?
        return this.lastDonationDate.isBefore(java.time.LocalDate.now().minusDays(90));
    }

}