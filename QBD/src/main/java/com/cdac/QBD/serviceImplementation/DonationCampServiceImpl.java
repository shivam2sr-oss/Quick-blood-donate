//package com.cdac.QBD.serviceImplementation;
//
//import com.cdac.QBD.dto.DonationCampCreateDto;
//import com.cdac.QBD.dto.DonationCampResponseDto;
//import com.cdac.QBD.entity.DonationCamp;
//import com.cdac.QBD.entity.Organization;
//import com.cdac.QBD.repository.DonationCampRepository;
//import com.cdac.QBD.repository.OrganizationRepository;
//import com.cdac.QBD.service.DonationCampService;
//import com.cdac.QBD.utils.constant.OrganizationType;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.stream.Collectors;
//
///**
// * Service Implementation for managing Donation Camps.
// */
//@Service
//@Transactional
//public class DonationCampServiceImpl implements DonationCampService {
//
//    private final DonationCampRepository donationCampRepository;
//    private final OrganizationRepository organizationRepository;
//
//    public DonationCampServiceImpl(DonationCampRepository donationCampRepository,
//                                   OrganizationRepository organizationRepository) {
//        this.donationCampRepository = donationCampRepository;
//        this.organizationRepository = organizationRepository;
//    }
//
//    @Override
//    public DonationCampResponseDto createCamp(DonationCampCreateDto dto) {
//        Organization organization = organizationRepository.findById(dto.getOrganizationId())
//                .orElseThrow(() -> new RuntimeException("Organization not found"));
//
//        // Only NODES can create camps
//        if (organization.getType() != OrganizationType.NODE) {
//            throw new RuntimeException("Only NODE organizations can create donation camps.");
//        }
//
//        DonationCamp camp = new DonationCamp();
//        camp.setOrganization(organization);
//
//        // Mapping fields from DTO
//        camp.setCampName(dto.getCampName());
//        camp.setAddress(dto.getAddress());
//        camp.setCampDate(dto.getCampDate());
//        camp.setStartTime(dto.getStartTime());
//        camp.setEndTime(dto.getEndTime());
//        camp.setProjectedUnits(dto.getProjectedUnits());
//
//        // Initial state
//        camp.setActualUnitsCollected(0);
//
//        return mapToResponseDto(donationCampRepository.save(camp));
//    }
//
//    @Override
//    public List<DonationCampResponseDto> getCampsByOrganization(Long organizationId) {
//        return donationCampRepository.findAll().stream()
//                .filter(c -> c.getOrganization().getId().equals(organizationId))
//                .map(this::mapToResponseDto)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public List<DonationCampResponseDto> getUpcomingCamps() {
//        LocalDate today = LocalDate.now();
//        // Show camps that are today or in the future
//        return donationCampRepository.findAll().stream()
//                .filter(c -> c.getCampDate() != null && !c.getCampDate().isBefore(today))
//                .map(this::mapToResponseDto)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public DonationCampResponseDto updateActualUnits(Long campId, int unitsCollected) {
//        DonationCamp camp = donationCampRepository.findById(campId)
//                .orElseThrow(() -> new RuntimeException("Camp not found"));
//
//        camp.setActualUnitsCollected(unitsCollected);
//        return mapToResponseDto(donationCampRepository.save(camp));
//    }
//
//    @Override
//    public DonationCampResponseDto mapToResponseDto(DonationCamp camp) {
//        DonationCampResponseDto dto = new DonationCampResponseDto();
//        dto.setId(camp.getId());
//        dto.setOrganizationId(camp.getOrganization().getId());
//        dto.setOrganizationName(camp.getOrganization().getName());
//        dto.setCampName(camp.getCampName());
//        dto.setAddress(camp.getAddress());
//        dto.setCampDate(camp.getCampDate());
//        dto.setStartTime(camp.getStartTime());
//        dto.setEndTime(camp.getEndTime());
//        dto.setProjectedUnits(camp.getProjectedUnits());
//        dto.setActualUnitsCollected(camp.getActualUnitsCollected());
//        return dto;
//    }
//}

package com.cdac.QBD.serviceImplementation;

import com.cdac.QBD.dto.DonationCampCreateDto;
import com.cdac.QBD.dto.DonationCampResponseDto;
import com.cdac.QBD.entity.DonationCamp;
import com.cdac.QBD.entity.Organization;
import com.cdac.QBD.repository.DonationCampRepository;
import com.cdac.QBD.repository.OrganizationRepository;
import com.cdac.QBD.service.DonationCampService;
import com.cdac.QBD.utils.constant.OrganizationType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DonationCampServiceImpl implements DonationCampService {

    private final DonationCampRepository donationCampRepository;
    private final OrganizationRepository organizationRepository;

    public DonationCampServiceImpl(DonationCampRepository donationCampRepository,
                                   OrganizationRepository organizationRepository) {
        this.donationCampRepository = donationCampRepository;
        this.organizationRepository = organizationRepository;
    }

    @Override
    public DonationCampResponseDto createCamp(DonationCampCreateDto dto) {
        Organization organization = organizationRepository.findById(dto.getOrganizationId())
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        if (organization.getType() != OrganizationType.NODE) {
            throw new RuntimeException("Only NODE organizations can create donation camps.");
        }

        DonationCamp camp = new DonationCamp();
        camp.setOrganization(organization);
        camp.setCampName(dto.getCampName());
        camp.setAddress(dto.getAddress());
        camp.setCampDate(dto.getCampDate());
        camp.setStartTime(dto.getStartTime());
        camp.setEndTime(dto.getEndTime());
        camp.setProjectedUnits(dto.getProjectedUnits());
        camp.setActualUnitsCollected(0);

        return mapToResponseDto(donationCampRepository.save(camp));
    }

    @Override
    public List<DonationCampResponseDto> getCampsByOrganization(Long organizationId) {
        // ✅ OPTIMIZED: Uses Database Query
        return donationCampRepository.findByOrganizationId(organizationId).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<DonationCampResponseDto> getUpcomingCamps() {
        // ✅ OPTIMIZED: Uses Database Query
        return donationCampRepository.findByCampDateGreaterThanEqual(LocalDate.now()).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public DonationCampResponseDto updateActualUnits(Long campId, int unitsCollected) {
        DonationCamp camp = donationCampRepository.findById(campId)
                .orElseThrow(() -> new RuntimeException("Camp not found"));

        camp.setActualUnitsCollected(unitsCollected);
        return mapToResponseDto(donationCampRepository.save(camp));
    }

    @Override
    public DonationCampResponseDto mapToResponseDto(DonationCamp camp) {
        DonationCampResponseDto dto = new DonationCampResponseDto();
        dto.setId(camp.getId());
        dto.setOrganizationId(camp.getOrganization().getId());
        dto.setOrganizationName(camp.getOrganization().getName());
        dto.setCampName(camp.getCampName());
        dto.setAddress(camp.getAddress());
        dto.setCampDate(camp.getCampDate());
        dto.setStartTime(camp.getStartTime());
        dto.setEndTime(camp.getEndTime());
        dto.setProjectedUnits(camp.getProjectedUnits());
        dto.setActualUnitsCollected(camp.getActualUnitsCollected());
        return dto;
    }
}