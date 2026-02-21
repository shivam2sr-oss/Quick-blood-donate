package com.cdac.QBD.service;

import com.cdac.QBD.dto.HospitalRequestCreateDto;
import com.cdac.QBD.dto.HospitalRequestResponseDto;
import com.cdac.QBD.entity.HospitalRequest;

import java.util.List;

/**
 * Hospital Request Service Interface
 * 
 * CORE FUNCTIONALITY - This is the heart of the Hospital module.
 * Handles blood requests from hospitals to CBBs.
 * 
 * IMPORTANT BOUNDARIES:
 * - Hospital asks, CBB decides & manages
 * - Hospital CANNOT directly update inventory
 * - Hospital CANNOT create blood transfers by itself
 * - Hospital CANNOT borrow from other CBBs directly
 * - Hospital CANNOT create alerts
 * - Only CBB can move request forward, not hospital
 */
public interface HospitalRequestService {

    /**
     * Create a new blood request (CORE FUNCTIONALITY)
     * 
     * Flow: Hospital → requests blood → CBB receives request
     * 
     * @param dto Request details (blood group, units, urgency)
     * @return Created request with PENDING status
     */
    HospitalRequestResponseDto createBloodRequest(HospitalRequestCreateDto dto);

    /**
     * View request status
     * @param requestId Request ID
     * @return Request details with current status
     */
    HospitalRequestResponseDto getRequestStatus(Long requestId);

    /**
     * View all past requests for a hospital
     * @param hospitalId Hospital organization ID
     * @return List of all requests (past and current)
     */
    List<HospitalRequestResponseDto> getAllRequestsByHospital(Long hospitalId);

    /**
     * View current pending requests for a hospital
     * @param hospitalId Hospital organization ID
     * @return List of pending requests
     */
    List<HospitalRequestResponseDto> getPendingRequestsByHospital(Long hospitalId);

    /**
     * View delivered requests for a hospital
     * @param hospitalId Hospital organization ID
     * @return List of delivered requests
     */
    List<HospitalRequestResponseDto> getDeliveredRequestsByHospital(Long hospitalId);

    /**
     * Get request entity by ID (internal use)
     * @param requestId Request ID
     * @return HospitalRequest entity
     */
    HospitalRequest getRequestById(Long requestId);

    /**
     * Check if request has breached 12-hour SLA (optional flag)
     * @param requestId Request ID
     * @return true if SLA breached, false otherwise
     */
    boolean isSlaBreach(Long requestId);
}
