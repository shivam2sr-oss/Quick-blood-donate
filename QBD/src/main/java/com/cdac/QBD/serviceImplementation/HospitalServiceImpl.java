package com.cdac.QBD.serviceImplementation;

import com.cdac.QBD.dto.HospitalRegisterDto;
import com.cdac.QBD.dto.HospitalRequestResponseDto;
import com.cdac.QBD.dto.HospitalResponseDto;
import com.cdac.QBD.entity.HospitalRequest;
import com.cdac.QBD.entity.Organization;
import com.cdac.QBD.repository.HospitalRequestRepository;
import com.cdac.QBD.repository.OrganizationRepository;
import com.cdac.QBD.service.HospitalService;
import com.cdac.QBD.utils.constant.OrganizationType;
import com.cdac.QBD.utils.constant.RequestStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Hospital Service Implementation
 * 
 * Handles hospital registration and profile management.
 * This service is OPTIONAL - mainly for administrative purposes.
 */
@Service
@Transactional
public class HospitalServiceImpl implements HospitalService {

    private final OrganizationRepository organizationRepository;
    private final HospitalRequestRepository hospitalRequestRepository;

    public HospitalServiceImpl(OrganizationRepository organizationRepository, HospitalRequestRepository hospitalRequestRepository) {
        this.organizationRepository = organizationRepository;
        this.hospitalRequestRepository = hospitalRequestRepository;
    }

    @Override
    public HospitalResponseDto registerHospital(HospitalRegisterDto dto) {
        // Validate parent CBB exists
        Organization parentCbb = organizationRepository.findById(dto.getParentCbbId())
                .orElseThrow(() -> new RuntimeException("CBB not found with ID: " + dto.getParentCbbId()));

        if (parentCbb.getType() != OrganizationType.CBB) {
            throw new RuntimeException("Parent organization must be a CBB");
        }

        // Create hospital organization
        Organization hospital = new Organization();
        hospital.setName(dto.getName());
        hospital.setType(OrganizationType.HOSPITAL);
        hospital.setCity(dto.getCity());
        hospital.setDistrict(dto.getDistrict());
        hospital.setState(dto.getState());
        hospital.setAddress(dto.getAddress());
        hospital.setContactNumber(dto.getContactNumber());
        hospital.setParentOrganization(parentCbb);

        Organization savedHospital = organizationRepository.save(hospital);

        return mapToResponseDto(savedHospital);
    }

    @Override
    public HospitalResponseDto getHospitalProfile(Long hospitalId) {
        Organization hospital = getHospitalOrganization(hospitalId);
        return mapToResponseDto(hospital);
    }

    @Override
    public HospitalResponseDto updateHospitalDetails(Long hospitalId, HospitalRegisterDto dto) {
        Organization hospital = getHospitalOrganization(hospitalId);

        // Update details
        hospital.setName(dto.getName());
        hospital.setCity(dto.getCity());
        hospital.setDistrict(dto.getDistrict());
        hospital.setState(dto.getState());
        hospital.setAddress(dto.getAddress());
        hospital.setContactNumber(dto.getContactNumber());

        // Update parent CBB if changed
        if (dto.getParentCbbId() != null && 
            !dto.getParentCbbId().equals(hospital.getParentOrganization().getId())) {
            
            Organization newCbb = organizationRepository.findById(dto.getParentCbbId())
                    .orElseThrow(() -> new RuntimeException("CBB not found with ID: " + dto.getParentCbbId()));

            if (newCbb.getType() != OrganizationType.CBB) {
                throw new RuntimeException("Parent organization must be a CBB");
            }

            hospital.setParentOrganization(newCbb);
        }

        Organization updatedHospital = organizationRepository.save(hospital);
        return mapToResponseDto(updatedHospital);
    }

    @Override
    public Organization getHospitalOrganization(Long hospitalId) {
        Organization hospital = organizationRepository.findById(hospitalId)
                .orElseThrow(() -> new RuntimeException("Hospital not found with ID: " + hospitalId));

        if (hospital.getType() != OrganizationType.HOSPITAL) {
            throw new RuntimeException("Organization is not a hospital");
        }

        if (hospital.getParentOrganization() == null) {
            throw new RuntimeException("Hospital must be connected to exactly one CBB");
        }

        return hospital;
    }

    @Override
    public List<HospitalRequestResponseDto> getHospitalRequests(Long hospitalId) {

        // Validate hospital exists
        Organization hospital = organizationRepository.findById(hospitalId)
                .orElseThrow(() -> new RuntimeException("Hospital not found"));

        // Fetch all requests for this hospital
        List<HospitalRequest> requests =
                hospitalRequestRepository.findByHospital(hospital);

        return requests.stream().map(request -> {

            // Calculate hours elapsed since request creation
            long hoursElapsed = java.time.Duration
                    .between(request.getRequestDate(), java.time.LocalDateTime.now())
                    .toHours();

            // SLA is 12 hours
            boolean slaBreached =
                    hoursElapsed > 12 &&
                            request.getStatus() != RequestStatus.DELIVERED;

            return new HospitalRequestResponseDto(
                    request.getId(),

                    request.getHospital().getId(),
                    request.getHospital().getName(),

                    request.getCbb().getId(),
                    request.getCbb().getName(),

                    request.getBloodGroup(),
                    request.getUnitsNeeded(),
                    request.getUrgency(),
                    request.getStatus(),
                    request.getRequestDate(),

                    slaBreached,
                    hoursElapsed
            );

        }).toList();
    }


    /**
     * Helper method to map Organization entity to HospitalResponseDto
     */
    private HospitalResponseDto mapToResponseDto(Organization hospital) {
        HospitalResponseDto dto = new HospitalResponseDto();
        dto.setId(hospital.getId());
        dto.setName(hospital.getName());
        dto.setCity(hospital.getCity());
        dto.setDistrict(hospital.getDistrict());
        dto.setState(hospital.getState());
        dto.setAddress(hospital.getAddress());
        dto.setContactNumber(hospital.getContactNumber());

        if (hospital.getParentOrganization() != null) {
            dto.setParentCbbId(hospital.getParentOrganization().getId());
            dto.setParentCbbName(hospital.getParentOrganization().getName());
        }

        return dto;
    }
}
