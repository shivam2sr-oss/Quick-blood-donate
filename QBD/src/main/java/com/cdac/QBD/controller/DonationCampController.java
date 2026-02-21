package com.cdac.QBD.controller;

import com.cdac.QBD.dto.DonationCampCreateDto;
import com.cdac.QBD.dto.DonationCampResponseDto;
import com.cdac.QBD.service.DonationCampService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for Donation Camp related APIs.
 * Thin controller: delegates all business logic to service layer.
 */
@RestController
@RequestMapping("/api/donation-camps")
public class DonationCampController {

    private final DonationCampService donationCampService;

    public DonationCampController(DonationCampService donationCampService) {
        this.donationCampService = donationCampService;
    }

    /**
     * Create a new Donation Camp (NODE only, validated in service).
     * Returns 201 CREATED because a new resource is created.
     */
    @PostMapping
    public ResponseEntity<DonationCampResponseDto> createCamp(
            @RequestBody DonationCampCreateDto dto) {

        DonationCampResponseDto response = donationCampService.createCamp(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Fetch all camps created by a specific Node.
     */
    @GetMapping("/organization/{orgId}")
    public ResponseEntity<List<DonationCampResponseDto>> getCampsByOrganization(
            @PathVariable Long orgId) {

        List<DonationCampResponseDto> camps =
                donationCampService.getCampsByOrganization(orgId);

        return ResponseEntity.ok(camps);
    }

    /**
     * Fetch all upcoming donation camps for donors to browse.
     */
    @GetMapping("/upcoming")
    public ResponseEntity<List<DonationCampResponseDto>> getUpcomingCamps() {

        List<DonationCampResponseDto> camps =
                donationCampService.getUpcomingCamps();

        return ResponseEntity.ok(camps);
    }

    /**
     * Update actual units collected after camp completion.
     */
    @PutMapping("/{campId}/units")
    public ResponseEntity<DonationCampResponseDto> updateActualUnits(
            @PathVariable Long campId,
            @RequestParam int unitsCollected) {

        DonationCampResponseDto response =
                donationCampService.updateActualUnits(campId, unitsCollected);

        return ResponseEntity.ok(response);
    }
}
