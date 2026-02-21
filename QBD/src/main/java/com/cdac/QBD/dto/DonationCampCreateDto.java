package com.cdac.QBD.dto;

import lombok.Data;
import java.time.LocalDate;

/**
 * DTO used to CREATE a Donation Camp.
 * This is intentionally kept simple and manual.
 *
 * IMPORTANT:
 * - No Alert reference here
 * - No Inventory logic here
 * - Camp creation is independent
 */
@Data
public class DonationCampCreateDto {

    /**
     * ID of the Node (Organization) organizing the camp.
     * Must be of type NODE.
     */
    private Long organizationId;

    /**
     * Name of the donation camp.
     * Example: "Symbiosis College Blood Drive"
     */
    private String campName;

    /**
     * Physical address where the camp will be conducted.
     */
    private String address;

    /**
     * Date on which the camp will take place.
     */
    private LocalDate campDate;

    /**
     * Start time of the camp (kept String for simplicity).
     * Example: "10:00 AM"
     */
    private String startTime;

    /**
     * End time of the camp.
     * Example: "04:00 PM"
     */
    private String endTime;

    /**
     * Expected blood units to be collected.
     * Used for planning and reporting.
     */
    private int projectedUnits;
}
