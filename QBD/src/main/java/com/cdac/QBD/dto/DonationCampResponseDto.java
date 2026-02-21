package com.cdac.QBD.dto;

import lombok.Data;
import java.time.LocalDate;

/**
 * DTO used to SEND Donation Camp data in responses.
 * Entity is never exposed directly.
 */
@Data
public class DonationCampResponseDto {

    private Long id;

    /**
     * Node details (minimal, safe for response)
     */
    private Long organizationId;
    private String organizationName;

    /**
     * Camp basic information
     */
    private String campName;
    private String address;
    private LocalDate campDate;
    private String startTime;
    private String endTime;

    /**
     * Tracking camp performance
     */
    private int projectedUnits;
    private int actualUnitsCollected;
}
