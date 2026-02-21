package com.cdac.QBD.controller;

import com.cdac.QBD.dto.HospitalRegisterDto;
import com.cdac.QBD.dto.HospitalResponseDto;
import com.cdac.QBD.service.HospitalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Hospital Controller
 * 
 * Handles hospital registration and profile management.
 * This is OPTIONAL functionality - mainly for administrative purposes.
 */
@RestController
@RequestMapping("/api/hospitals")
public class HospitalController {

    private final HospitalService hospitalService;

    public HospitalController(HospitalService hospitalService) {
        this.hospitalService = hospitalService;
    }

    /**
     * Register a new hospital
     * POST /api/hospitals/register
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerHospital(
            @RequestBody HospitalRegisterDto dto) {
        
        HospitalResponseDto response = hospitalService.registerHospital(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get hospital profile
     * GET /api/hospitals/{hospitalId}
     */
    @GetMapping("/{hospitalId}")
    public ResponseEntity<HospitalResponseDto> getHospitalProfile(
            @PathVariable Long hospitalId) {
        
        HospitalResponseDto response = hospitalService.getHospitalProfile(hospitalId);
        return ResponseEntity.ok(response);
    }

    /**
     * Update hospital details
     * PUT /api/hospitals/{hospitalId}
     */
    @PutMapping("/{hospitalId}")
    public ResponseEntity<HospitalResponseDto> updateHospitalDetails(
            @PathVariable Long hospitalId,
            @RequestBody HospitalRegisterDto dto) {
        
        HospitalResponseDto response = hospitalService.updateHospitalDetails(hospitalId, dto);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/requests/{hospitalId}")
    public ResponseEntity<?> getHospitalRequests(@PathVariable Long hospitalId) {
        return ResponseEntity.ok(hospitalService.getHospitalRequests(hospitalId));
    }
}
