package com.cdac.QBD.dto;

import lombok.Data;

/**
 * DTO for Hospital Registration
 */
@Data
public class HospitalRegisterDto {
    
    private String name;
    
    private String city;
    
    private String district;
    
    private String state;
    
    private String address;
    
    private String contactNumber;
    
    /**
     * The CBB that this hospital will be assigned to (city-level)
     * Must be exactly one CBB
     */
    private Long parentCbbId;
}
