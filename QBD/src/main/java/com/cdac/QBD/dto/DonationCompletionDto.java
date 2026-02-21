package com.cdac.QBD.dto;

import lombok.Data;

/**
 * DTO used to mark a donation as completed.
 * Called only after an APPROVED donation is physically done.
 */
@Data
public class DonationCompletionDto {

    /**
     * ID of the donation request being completed.
     */
    private Long requestId;

    /**
     * Actual number of blood units collected from the donor.
     * Must be greater than zero.
     */
    private int unitsCollected;
}
