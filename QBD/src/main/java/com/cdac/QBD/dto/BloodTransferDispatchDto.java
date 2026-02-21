package com.cdac.QBD.dto;

import com.cdac.QBD.utils.constant.BloodGroup;
import lombok.Data;

@Data
public class BloodTransferDispatchDto {
    private Long fromOrgId;
    private Long toOrgId;
    private BloodGroup bloodGroup;
    private int quantity;
    private String transferType; // e.g., "Routine Collection"
}