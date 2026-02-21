package com.cdac.QBD.controller;

import com.cdac.QBD.dto.*;
import com.cdac.QBD.entity.DonationRequest;
import com.cdac.QBD.service.DonorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/donors")
public class DonorController {

    private final DonorService donorService;

    public DonorController(DonorService donorService) {
        this.donorService = donorService;
    }





    @GetMapping("/current")
    public ResponseEntity<DonorResponseDto> getCurrentDonorProfile(Principal principal) {
        String email = principal.getName();
        return ResponseEntity.ok(donorService.getDonorProfileByEmail(email));
    }

    /**
     * Get donor profile.
     */
    @GetMapping("/{donorId}")
    public ResponseEntity<DonorResponseDto> getDonor(@PathVariable Long donorId) {
        return ResponseEntity.ok(donorService.getDonorProfile(donorId));
    }

    /**
     * Check donor eligibility (90-day rule).
     */
    @GetMapping("/{donorId}/eligibility")
    public ResponseEntity<EligibilityResponseDto> checkEligibility(
            @PathVariable Long donorId) {

        boolean eligible = donorService.isEligible(donorId);
        return ResponseEntity.ok(new EligibilityResponseDto(eligible));
    }

    /**
     * Get donor donation history as safe DTOs (not entities).
     */
    @GetMapping("/{donorId}/donations")
    public ResponseEntity<List<DonationRequestResponseDto>> getDonationHistory(
            @PathVariable Long donorId) {

        List<DonationRequestResponseDto> response =
                donorService.getDonationHistory(donorId)
                        .stream()
                        .map(req -> {
                            DonationRequestResponseDto dto = new DonationRequestResponseDto();
                            dto.setId(req.getId());
                            dto.setDonorId(req.getDonor().getId());
                            dto.setDonorName(req.getDonor().getFullName());
                            dto.setNodeId(req.getNode().getId());
                            dto.setNodeName(req.getNode().getName());
                            dto.setStatus(req.getStatus());
                            dto.setDonationDate(req.getDonationDate());
                            dto.setUnitsCollected(req.getUnitsCollected());
                            dto.setMedicalRemarks(req.getMedicalRemarks());
                            return dto;
                        })
                        .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}
