package com.cdac.QBD.entity;

import com.cdac.QBD.utils.constant.BloodGroup;
import com.cdac.QBD.utils.constant.RequestStatus;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "blood_transfers")
public class BloodTransfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Sender (Could be a Node or a CBB)
    @ManyToOne
    @JoinColumn(name = "from_org_id", nullable = false)
    private Organization fromOrg;

    // Receiver (Usually the CBB)
    @ManyToOne
    @JoinColumn(name = "to_org_id", nullable = false)
    private Organization toOrg;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BloodGroup bloodGroup;

    private int quantity; // How many bags are being moved

    @Enumerated(EnumType.STRING)
    private RequestStatus status; // DISPATCHED -> RECEIVED

    private LocalDateTime transferDate;

    // e.g., "Routine Collection from Node" or "Emergency Stock Transfer"
    private String transferType;
}
