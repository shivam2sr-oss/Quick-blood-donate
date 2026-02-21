package com.cdac.QBD.entity;

import com.cdac.QBD.utils.constant.BloodGroup;
import com.cdac.QBD.utils.constant.UrgencyLevel;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "alerts")
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The CBB raising the alert
    @ManyToOne
//    @JoinColumn(name = "org_id", nullable = false)
    private HospitalRequest hospitalRequest;

    @ManyToOne
    private Organization raisingOrganization;
    @Enumerated(EnumType.STRING)
    private BloodGroup bloodGroup;

    private String targetDistrict; // The district where help is needed

    private String message;

    private boolean isResolved;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private UrgencyLevel urgency; // NORMAL, CRITICAL

    private int escalationLevel;
    // 0 = CITY
    // 1 = DISTRICT
    // 2 = STATE

}
