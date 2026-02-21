package com.cdac.QBD.dto;

import com.cdac.QBD.utils.constant.BloodGroup;
import lombok.Data;

import java.time.LocalDate;

@Data
public class DonorRegisterDto {

    private String email;
    private String password;
    private String fullName;

    private BloodGroup bloodGroup;

    private LocalDate dob;
    private String gender;
    private Double weight;

    private String address;
    private String contactNumber;
    private String aadharNumber;
}
