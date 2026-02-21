package com.cdac.QBD.entity;

import com.cdac.QBD.utils.constant.RequestStatus;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "donation_requests")
public class DonationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "donor_id", nullable = false)
    private User donor;

    // The Node where donation happens
    @ManyToOne
    @JoinColumn(name = "node_id", nullable = false)
    private Organization node;

    private LocalDate donationDate;

    @Enumerated(EnumType.STRING)
    private RequestStatus status; // PENDING -> APPROVED -> COMPLETED

    private int unitsCollected;

    @Column(columnDefinition = "TEXT")
    private String medicalRemarks; // Notes on eligibility/rejection
}
