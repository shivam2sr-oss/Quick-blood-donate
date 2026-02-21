package com.cdac.QBD.dto;

import lombok.Data;

@Data
public class DonationRequestDto {

    private Long donorId;
    private Long nodeId;
    private int unitsCollected;
    private String medicalRemarks;
}
