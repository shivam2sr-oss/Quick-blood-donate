package com.cdac.QBD.service;

import com.cdac.QBD.dto.DonationCampCreateDto;
import com.cdac.QBD.dto.DonationCampResponseDto;
import com.cdac.QBD.entity.DonationCamp;

import java.util.List;

public interface DonationCampService {

    DonationCampResponseDto createCamp(DonationCampCreateDto dto);

    List<DonationCampResponseDto> getCampsByOrganization(Long organizationId);

    List<DonationCampResponseDto> getUpcomingCamps();

    DonationCampResponseDto updateActualUnits(Long campId, int unitsCollected);

    DonationCampResponseDto mapToResponseDto(DonationCamp camp);
}
