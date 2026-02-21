package com.cdac.QBD.service;

import com.cdac.QBD.dto.HospitalRegisterDto;
import com.cdac.QBD.dto.HospitalRequestResponseDto;
import com.cdac.QBD.dto.HospitalResponseDto;
import com.cdac.QBD.entity.HospitalRequest;
import com.cdac.QBD.entity.Organization;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * Hospital Service Interface
 * 
 * Handles hospital registration and profile management.
 * This is optional - mainly for managing hospital profiles.
 */
public interface HospitalService {

    /**
     * Register a new hospital in the system
     * @param dto Hospital registration data
     * @return Registered hospital details
     */
    HospitalResponseDto registerHospital(HospitalRegisterDto dto);

    /**
     * Get hospital profile by ID
     * @param hospitalId The hospital organization ID
     * @return Hospital details
     */
    HospitalResponseDto getHospitalProfile(Long hospitalId);

    /**
     * Update hospital details
     * @param hospitalId The hospital organization ID
     * @param dto Updated hospital data
     * @return Updated hospital details
     */
    HospitalResponseDto updateHospitalDetails(Long hospitalId, HospitalRegisterDto dto);

    /**
     * Get the hospital organization entity
     * @param hospitalId The hospital organization ID
     * @return Hospital organization entity
     */
    Organization getHospitalOrganization(Long hospitalId);

     List<HospitalRequestResponseDto> getHospitalRequests(Long hospitalId);
}
