package com.cdac.QBD.dto;

import com.cdac.QBD.utils.constant.BloodGroup;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class DonorResponseDto {

    private Long id;
    private String email;
    private String fullName;

    private BloodGroup bloodGroup;

    private LocalDate lastDonationDate;
    private boolean eligible;

    private String contactNumber;
    private String address;

    private List<String> medicalHistory;
}
