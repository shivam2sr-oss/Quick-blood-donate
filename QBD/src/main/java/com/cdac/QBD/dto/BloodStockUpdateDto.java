package com.cdac.QBD.dto;

import com.cdac.QBD.utils.constant.BloodGroup;
import lombok.Data;

@Data
public class BloodStockUpdateDto {
    private Long cbbId;
    private BloodGroup bloodGroup;
    private int quantity;
    private String remarks;
}