package com.cdac.QBD.controller;

import com.cdac.QBD.dto.*;
import com.cdac.QBD.service.DonationService;
import com.cdac.QBD.utils.constant.RequestStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Handles full donation lifecycle APIs.
 * ENDPOINTS:
 * 1. POST /api/donations/create           -> Donor initiates request
 * 2. GET  /api/donations/donor/{id}       -> Donor views history
 * 3. GET  /api/donations/node/{id}/{status} -> Node views filtered lists (PENDING vs APPROVED)
 * 4. PUT  /api/donations/approve          -> Node approves/rejects
 * 5. PUT  /api/donations/complete         -> Node marks collection complete
 */
@RestController
@RequestMapping("/api/donations")
@RequiredArgsConstructor
public class DonationController {

    private final DonationService donationService;

    /**
     * ✅ 1. CREATE DONATION REQUEST
     * Called by: Donor Dashboard (when "Confirm Donation" is clicked)
     */
    @PostMapping("/create")
    public ResponseEntity<DonationRequestResponseDto> createDonationRequest(
            @RequestBody DonationRequestCreateDto dto) {

        DonationRequestResponseDto response =
                donationService.createDonationRequest(dto);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * ✅ 2. GET DONOR HISTORY
     * Called by: Donor Dashboard (to fill the History Table)
     */
    @GetMapping("/donor/{donorId}")
    public ResponseEntity<List<DonationRequestResponseDto>> getDonationsByDonor(
            @PathVariable Long donorId) {

        List<DonationRequestResponseDto> response =
                donationService.getRequestsByDonor(donorId);

        return ResponseEntity.ok(response);
    }

    /**
     * ✅ 3. GET REQUESTS FOR NODE (FILTERED BY STATUS)
     * Called by: Node Staff Dashboard (Tabs: 'Pending Approval' vs 'Ready for Collection')
     * Usage: /api/donations/node/1/PENDING  or  /api/donations/node/1/APPROVED
     */
    @GetMapping("/node/{nodeId}/{status}")
    public ResponseEntity<List<DonationRequestResponseDto>> getRequestsByNodeAndStatus(
            @PathVariable Long nodeId,
            @PathVariable String status) {

        // Convert String (e.g., "PENDING") to Enum
        RequestStatus reqStatus;
        try {
            reqStatus = RequestStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid Status: " + status);
        }

        List<DonationRequestResponseDto> response =
                donationService.getRequestsByNodeAndStatus(nodeId, reqStatus);

        return ResponseEntity.ok(response);
    }

    // (Keeping this for backward compatibility if you need it, but the one above covers it)
    @GetMapping("/node/{nodeId}/pending")
    public ResponseEntity<List<DonationRequestResponseDto>> getPendingRequestsForNode(
            @PathVariable Long nodeId) {
        return ResponseEntity.ok(donationService.getPendingRequestsForNode(nodeId));
    }

    /**
     * ✅ 4. APPROVE OR REJECT
     * Called by: Node Staff Dashboard (Approve/Reject Buttons)
     */
    @PutMapping("/approve")
    public ResponseEntity<DonationRequestResponseDto> approveOrRejectDonation(
            @RequestBody DonationApprovalDto dto) {

        DonationRequestResponseDto response =
                donationService.approveOrRejectDonation(dto);

        return ResponseEntity.ok(response);
    }

    /**
     * ✅ 5. COMPLETE DONATION
     * Called by: Node Staff Dashboard (After blood is collected)
     */
    @PutMapping("/complete")
    public ResponseEntity<DonationRequestResponseDto> completeDonation(
            @RequestBody DonationCompletionDto dto) {

        DonationRequestResponseDto response =
                donationService.completeDonation(dto);

        return ResponseEntity.ok(response);
    }
}