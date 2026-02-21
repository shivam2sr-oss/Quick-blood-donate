package com.cdac.QBD.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Hospital Response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HospitalResponseDto {
    
    private Long id;
    
    private String name;
    
    private String city;
    
    private String district;
    
    private String state;
    
    private String address;
    
    private String contactNumber;
    
    private Long parentCbbId;
    
    private String parentCbbName;
}
