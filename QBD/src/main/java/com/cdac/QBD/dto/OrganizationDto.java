package com.cdac.QBD.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationDto {
    private Long id;
    private String name;
    private String address;
    private String contactNumber;
    private String city;
    private String district;
    private String state;
    private String type;
    private Long parentOrganizationId;
}
