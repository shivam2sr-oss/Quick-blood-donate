package com.cdac.QBD.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequestDTO {
    private String email;
    private String password;
    private String role;
    private String bloodGroup;
    private Double weight;
    private String fullName;
    private String aadharNumber;
    private LocalDate dob;
    private String gender;
    private String address;
    private String contactNumber;
    private String district;
    private String city;
    private String parentOrganizationId;
    private List<String> medicalHistory;
}
