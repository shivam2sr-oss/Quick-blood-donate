package com.cdac.QBD.controller;

import com.cdac.QBD.dto.HospitalRequestCreateDto;
import com.cdac.QBD.dto.HospitalRequestResponseDto;
import com.cdac.QBD.service.HospitalRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Hospital Request Controller
 * 
 * CORE FUNCTIONALITY - Heart of the Hospital module
 * 
 * Handles blood requests from hospitals to CBBs.
 * 
 * IMPORTANT BOUNDARIES:
 * - Hospital can only CREATE requests (status: PENDING)
 * - Hospital can VIEW request history and status
 * - Hospital CANNOT move request forward (only CBB can: PENDING → DISPATCHED → DELIVERED)
 * - Hospital CANNOT update inventory, create transfers, or create alerts
 */
@RestController
@RequestMapping("/api/hospital-requests")
public class HospitalRequestController {

    private final HospitalRequestService hospitalRequestService;

    public HospitalRequestController(HospitalRequestService hospitalRequestService) {
        this.hospitalRequestService = hospitalRequestService;
    }

    /**
     * Create a new blood request (CORE FUNCTIONALITY)
     * POST /api/hospital-requests
     * 
     * Flow: Hospital → requests blood → CBB receives request
     * Status at creation: PENDING
     * 
     * IMPORTANT: No inventory change here. Hospital asks, CBB decides & manages.
     */
    @PostMapping
    public ResponseEntity<HospitalRequestResponseDto> createBloodRequest(
            @RequestBody HospitalRequestCreateDto dto) {
        
        HospitalRequestResponseDto response = hospitalRequestService.createBloodRequest(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * View request status
     * GET /api/hospital-requests/{requestId}
     * 
     * Returns current status and tracks timestamps
     */
    @GetMapping("/{requestId}")
    public ResponseEntity<HospitalRequestResponseDto> getRequestStatus(
            @PathVariable Long requestId) {
        
        HospitalRequestResponseDto response = hospitalRequestService.getRequestStatus(requestId);
        return ResponseEntity.ok(response);
    }

    /**
     * View all past requests for a hospital
     * GET /api/hospital-requests/hospital/{hospitalId}
     * 
     * Essential for:
     * - Audits
     * - Hospital dashboard
     * - Reporting
     */
    @GetMapping("/hospital/{hospitalId}")
    public ResponseEntity<List<HospitalRequestResponseDto>> getAllRequestsByHospital(
            @PathVariable Long hospitalId) {
        
        List<HospitalRequestResponseDto> requests = 
                hospitalRequestService.getAllRequestsByHospital(hospitalId);
        return ResponseEntity.ok(requests);
    }

    /**
     * View current pending requests for a hospital
     * GET /api/hospital-requests/hospital/{hospitalId}/pending
     */
    @GetMapping("/hospital/{hospitalId}/pending")
    public ResponseEntity<List<HospitalRequestResponseDto>> getPendingRequestsByHospital(
            @PathVariable Long hospitalId) {
        
        List<HospitalRequestResponseDto> requests = 
                hospitalRequestService.getPendingRequestsByHospital(hospitalId);
        return ResponseEntity.ok(requests);
    }

    /**
     * View delivered requests for a hospital
     * GET /api/hospital-requests/hospital/{hospitalId}/delivered
     */
    @GetMapping("/hospital/{hospitalId}/delivered")
    public ResponseEntity<List<HospitalRequestResponseDto>> getDeliveredRequestsByHospital(
            @PathVariable Long hospitalId) {
        
        List<HospitalRequestResponseDto> requests = 
                hospitalRequestService.getDeliveredRequestsByHospital(hospitalId);
        return ResponseEntity.ok(requests);
    }

    /**
     * Check if request has breached 12-hour SLA (optional flag)
     * GET /api/hospital-requests/{requestId}/sla-breach
     * 
     * As per requirement: "CBB must provide blood within 12 hours whatever happens"
     * 
     * IMPORTANT: Actual enforcement belongs to CBB logic, not hospital.
     * This is just for awareness/tracking.
     */
    @GetMapping("/{requestId}/sla-breach")
    public ResponseEntity<Boolean> checkSlaBreach(
            @PathVariable Long requestId) {
        
        boolean breached = hospitalRequestService.isSlaBreach(requestId);
        return ResponseEntity.ok(breached);
    }
}
