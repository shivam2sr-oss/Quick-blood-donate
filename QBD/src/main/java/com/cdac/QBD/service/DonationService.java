package com.cdac.QBD.service;

import com.cdac.QBD.dto.DonationApprovalDto;
import com.cdac.QBD.dto.DonationCompletionDto;
import com.cdac.QBD.dto.DonationRequestCreateDto;
import com.cdac.QBD.dto.DonationRequestResponseDto;
import com.cdac.QBD.entity.DonationRequest;
import com.cdac.QBD.utils.constant.RequestStatus;

import java.util.List;

public interface DonationService {
    DonationRequestResponseDto createDonationRequest(DonationRequestCreateDto dto);

    List<DonationRequestResponseDto> getRequestsByDonor(Long donorId);

    List<DonationRequestResponseDto> getPendingRequestsForNode(Long nodeId);

    // ✅ NEW METHOD: Supports the Tabs in Node Dashboard (Pending vs Approved)
    List<DonationRequestResponseDto> getRequestsByNodeAndStatus(Long nodeId, RequestStatus status);

    DonationRequestResponseDto approveOrRejectDonation(DonationApprovalDto dto);

    DonationRequestResponseDto completeDonation(DonationCompletionDto dto);

    DonationRequestResponseDto mapToResponseDto(DonationRequest request);
}
