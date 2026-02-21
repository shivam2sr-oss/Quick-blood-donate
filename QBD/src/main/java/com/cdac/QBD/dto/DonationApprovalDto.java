package com.cdac.QBD.dto;

import lombok.Data;

/**
 * DTO used by Node staff to approve or reject a donation request.
 * Physical medical checks happen offline; this DTO records the decision.
 */
@Data
public class DonationApprovalDto {

    /**
     * ID of the donation request to be approved or rejected.
     */
    private Long requestId;

    /**
     * Decision flag:
     * true  → approve donation
     * false → reject donation
     */
    private boolean approve;

    /**
     * Medical remarks or rejection reason.
     * Optional but recommended for audit and traceability.
     */
    private String medicalRemarks;

    private Long nodeId;
}
