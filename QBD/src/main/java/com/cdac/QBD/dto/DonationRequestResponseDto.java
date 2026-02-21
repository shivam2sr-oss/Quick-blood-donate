package com.cdac.QBD.dto;

import com.cdac.QBD.utils.constant.RequestStatus;
import lombok.Data;

import java.time.LocalDate;

/**
 * DTO returned in responses.
 * Used to populate the Donation History table in the Dashboard.
 */
@Data
public class DonationRequestResponseDto {

    private Long id;

    // Donor Info
    private Long donorId;
    private String donorName;

    // Node Info (To show "Location" in the table)
    private Long nodeId;
    private String nodeName;

    // Status (PENDING, APPROVED, REJECTED, COMPLETED)
    private RequestStatus status;

    // Date (Null until Completed)
    private LocalDate donationDate;

    // Units (0 until Completed)
    private int unitsCollected;

    // Remarks (e.g. "Low Hemoglobin" if rejected)
    private String medicalRemarks;
}