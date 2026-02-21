package com.cdac.QBD.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * DTO used when a Donor creates a new donation request.
 * Expected JSON: { "donorId": 1, "nodeId": 5 }
 */
@Data
public class DonationRequestCreateDto {

    /**
     * ID of the donor (User with role = DONOR)
     */
    private Long donorId;

    /**
     * ID of the Node (Organization with type = NODE)
     * This is selected from the dropdown in the modal.
     */
    private Long nodeId;

    private LocalDate preferredDate;
}