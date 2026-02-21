package com.cdac.QBD.service;

import com.cdac.QBD.dto.DonorRegisterDto;
import com.cdac.QBD.dto.DonorResponseDto;
import com.cdac.QBD.entity.DonationRequest;
import com.cdac.QBD.entity.User;

import java.util.List;

public interface DonorService {


    DonorResponseDto getDonorProfile(Long donorId);

    DonorResponseDto getDonorProfileByEmail(String email);

    boolean isEligible(Long donorId);

    void updateAfterDonation(Long donorId);

    List<DonationRequest> getDonationHistory(Long donorId);

    User getDonorEntity(Long donorId);

    DonorResponseDto mapToResponseDto(User donor);

    //DonorResponseDto registerDonor(DonorRegisterDto dto);
}
