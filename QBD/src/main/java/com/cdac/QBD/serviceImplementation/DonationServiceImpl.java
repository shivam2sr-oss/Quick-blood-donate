package com.cdac.QBD.serviceImplementation;

import com.cdac.QBD.dto.*;
import com.cdac.QBD.entity.DonationRequest;
import com.cdac.QBD.entity.Organization;
import com.cdac.QBD.entity.User;
import com.cdac.QBD.repository.DonationRequestRepository;
import com.cdac.QBD.repository.OrganizationRepository;
import com.cdac.QBD.repository.UserRepository;
import com.cdac.QBD.service.BloodTransferService;
import com.cdac.QBD.service.DonationService;
import com.cdac.QBD.service.DonorService;
import com.cdac.QBD.utils.constant.OrganizationType;
import com.cdac.QBD.utils.constant.RequestStatus;
import com.cdac.QBD.utils.constant.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing Blood Donations.
 * Handles: Request Creation -> Approval/Rejection -> Completion.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class DonationServiceImpl implements DonationService {

    private final DonationRequestRepository donationRequestRepository;
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final DonorService donorService;

    // ✅ NEW: Service needed to handle automatic transfer to CBB
    private final BloodTransferService bloodTransferService;

    // ----------------------------------------------------------------
    // 1. CREATE REQUEST (With Date Picker Support)
    // ----------------------------------------------------------------
    @Override
    public DonationRequestResponseDto createDonationRequest(DonationRequestCreateDto dto) {

        // Validate Donor
        User donor = userRepository.findById(dto.getDonorId())
                .orElseThrow(() -> new RuntimeException("Donor not found"));

        if (donor.getRole() != UserRole.DONOR) {
            throw new RuntimeException("User is not a registered donor.");
        }

        // Delegate Eligibility Check (90-day rule)
        // Ensure User.java has the virtual getter isEligible()
        if (!donorService.isEligible(donor.getId())) {
            throw new RuntimeException("Donor is in cooling period.");
        }

        // Prevent Duplicate Active Requests
        boolean hasActive = donationRequestRepository.findByDonor(donor).stream()
                .anyMatch(r -> r.getStatus() == RequestStatus.PENDING || r.getStatus() == RequestStatus.APPROVED);

        if (hasActive) {
            throw new RuntimeException("You already have an active donation request.");
        }

        // Validate Node
        Organization node = organizationRepository.findById(dto.getNodeId())
                .orElseThrow(() -> new RuntimeException("Collection Node not found"));

        if (node.getType() != OrganizationType.NODE) {
            throw new RuntimeException("Selected organization is not a valid Collection Node.");
        }

        // Save Request
        DonationRequest request = new DonationRequest();
        request.setDonor(donor);
        request.setNode(node);
        request.setStatus(RequestStatus.PENDING);

        // Date Handling: Use preferred date if provided, otherwise default to today
        if (dto.getPreferredDate() != null) {
            request.setDonationDate(dto.getPreferredDate());
        } else {
            request.setDonationDate(LocalDate.now());
        }

        return mapToResponseDto(donationRequestRepository.save(request));
    }

    // ----------------------------------------------------------------
    // 2. FETCH HISTORY (Donor) & PENDING (Node)
    // ----------------------------------------------------------------
    @Override
    public List<DonationRequestResponseDto> getRequestsByDonor(Long donorId) {
        User donor = userRepository.findById(donorId)
                .orElseThrow(() -> new RuntimeException("Donor not found"));

        return donationRequestRepository.findByDonor(donor).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<DonationRequestResponseDto> getPendingRequestsForNode(Long nodeId) {
        // Keeping this for backward compatibility if needed
        return getRequestsByNodeAndStatus(nodeId, RequestStatus.PENDING);
    }

    // ✅ Supports Dashboard Tabs (Pending vs Approved)
    @Override
    public List<DonationRequestResponseDto> getRequestsByNodeAndStatus(Long nodeId, RequestStatus status) {
        return donationRequestRepository.findAll().stream()
                .filter(r -> r.getNode().getId().equals(nodeId) && r.getStatus() == status)
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    // ----------------------------------------------------------------
    // 3. APPROVE OR REJECT
    // ----------------------------------------------------------------
    @Override
    public DonationRequestResponseDto approveOrRejectDonation(DonationApprovalDto dto) {
        DonationRequest request = donationRequestRepository.findById(dto.getRequestId())
                .orElseThrow(() -> new RuntimeException("Request not found"));

        // Security: Ensure the approving Node matches the request's Node
        if (!request.getNode().getId().equals(dto.getNodeId())) {
            throw new RuntimeException("Unauthorized: This request belongs to a different Node.");
        }

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new RuntimeException("Request is not PENDING.");
        }

        if (dto.isApprove()) {
            request.setStatus(RequestStatus.APPROVED);
        } else {
            request.setStatus(RequestStatus.REJECTED);
        }

        request.setMedicalRemarks(dto.getMedicalRemarks());
        return mapToResponseDto(donationRequestRepository.save(request));
    }

    // ----------------------------------------------------------------
    // 4. COMPLETE DONATION (Integration with Inventory)
    // ----------------------------------------------------------------
    @Override
    public DonationRequestResponseDto completeDonation(DonationCompletionDto dto) {
        System.out.println(dto);
        DonationRequest request = donationRequestRepository.findById(dto.getRequestId())
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (request.getStatus() != RequestStatus.APPROVED) {
            throw new RuntimeException("Cannot complete: Request is not Approved.");
        }

        // 1. Update Request
        request.setUnitsCollected(dto.getUnitsCollected());
        request.setDonationDate(LocalDate.now()); // Actual collection day
        request.setStatus(RequestStatus.COMPLETED);

        // 2. Update Donor History (Resets the 90-day timer)
        User donor = request.getDonor();
        donor.setLastDonationDate(LocalDate.now());
        userRepository.save(donor);

        // ✅ 3. NEW: AUTOMATICALLY DISPATCH TO CBB
        // Find the CBB for this Node (assuming same City logic)
        Organization node = request.getNode();

        // Safety check: Ensure donor has a blood group recorded
        if (donor.getBloodGroup() == null) {
            throw new RuntimeException("Donor Blood Group is missing. Cannot process inventory.");
        }

        Organization cbb = organizationRepository.findAll().stream()
                .filter(o -> o.getType() == OrganizationType.CBB && o.getCity().equalsIgnoreCase(node.getCity()))
                .findFirst()
                // Fallback: If no CBB in the city, just pick the first available CBB
                .orElse(organizationRepository.findAll().stream()
                        .filter(o -> o.getType() == OrganizationType.CBB)
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("No CBB found to transfer blood.")));


        // Create Transfer Record (Node -> CBB)
        // We assume 1 bag (unit) is transferred per donation
        bloodTransferService.dispatchTransfer(
                node,
                cbb,
                donor.getBloodGroup(),
                1, // 1 Unit/Bag
                "Donation Collection ID: " + request.getId()
        );

        return mapToResponseDto(donationRequestRepository.save(request));
    }

    // ----------------------------------------------------------------
    // 5. HELPER: MAPPER
    // ----------------------------------------------------------------
    @Override
    public DonationRequestResponseDto mapToResponseDto(DonationRequest request) {
        DonationRequestResponseDto dto = new DonationRequestResponseDto();
        dto.setId(request.getId());
        dto.setDonorId(request.getDonor().getId());
        dto.setDonorName(request.getDonor().getFullName());
        dto.setNodeId(request.getNode().getId());
        dto.setNodeName(request.getNode().getName());
        dto.setStatus(request.getStatus());
        dto.setDonationDate(request.getDonationDate());
        dto.setUnitsCollected(request.getUnitsCollected());
        dto.setMedicalRemarks(request.getMedicalRemarks());
        return dto;
    }
}