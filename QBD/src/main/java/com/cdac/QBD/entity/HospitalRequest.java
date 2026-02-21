package com.cdac.QBD.entity;

import com.cdac.QBD.utils.constant.BloodGroup;
import com.cdac.QBD.utils.constant.RequestStatus;
import com.cdac.QBD.utils.constant.UrgencyLevel;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "hospital_requests")
public class HospitalRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "hospital_id", nullable = false)
    private Organization hospital;

    // The CBB fulfilling the request
    @ManyToOne
    @JoinColumn(name = "cbb_id", nullable = false)
    private Organization cbb;

    @Enumerated(EnumType.STRING)
    private BloodGroup bloodGroup;

    private int unitsNeeded;

    @Enumerated(EnumType.STRING)
    private UrgencyLevel urgency; // NORMAL vs CRITICAL

    @Enumerated(EnumType.STRING)
    private RequestStatus status; // PENDING -> DISPATCHED -> DELIVERED

    private LocalDateTime requestDate;
}