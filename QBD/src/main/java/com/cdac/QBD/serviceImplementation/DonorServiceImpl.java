package com.cdac.QBD.serviceImplementation;

import com.cdac.QBD.dto.DonorResponseDto;
import com.cdac.QBD.entity.DonationRequest;
import com.cdac.QBD.entity.MedicalHistory;
import com.cdac.QBD.entity.User;
import com.cdac.QBD.repository.DonationRequestRepository;
import com.cdac.QBD.repository.UserRepository;
import com.cdac.QBD.service.DonorService;
import com.cdac.QBD.utils.constant.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DonorServiceImpl implements DonorService {

    private final UserRepository userRepository;
    private final DonationRequestRepository donationRequestRepository;

    // ❌ REMOVED: registerDonor method.
    // Logic is now properly handled in UserServiceImpl.

    /**
     * Fetch donor profile (converting Entity -> DTO)
     */
    @Override
    public DonorResponseDto getDonorProfile(Long donorId) {
        User donor = getDonorEntity(donorId);
        return mapToResponseDto(donor);
    }

    @Override
    public DonorResponseDto getDonorProfileByEmail(String email) {
        User donor = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Donor not found"));

        if (donor.getRole() != UserRole.DONOR) {
            throw new RuntimeException("User is not a donor");
        }
        return mapToResponseDto(donor);
    }

    /**
     * BUSINESS RULE: 90-Day Gap
     * Checks if the donor is medically allowed to donate today.
     */
    @Override
    public boolean isEligible(Long donorId) {
        User donor = getDonorEntity(donorId);
        LocalDate lastDonation = donor.getLastDonationDate();

        // If never donated, they are eligible
        if (lastDonation == null) {
            return true;
        }

        // Check if 90 days have passed
        return lastDonation.plusDays(90).isBefore(LocalDate.now())
                || lastDonation.plusDays(90).isEqual(LocalDate.now());
    }

    /**
     * Helper to get donation history
     */
    @Override
    public List<DonationRequest> getDonationHistory(Long donorId) {
        User donor = getDonorEntity(donorId);
        return donationRequestRepository.findByDonor(donor);
    }

    // --- INTERNAL HELPERS ---

    public User getDonorEntity(Long donorId) {
        User donor = userRepository.findById(donorId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (donor.getRole() != UserRole.DONOR) {
            throw new RuntimeException("User is not a donor");
        }
        return donor;
    }

    public DonorResponseDto mapToResponseDto(User donor) {
        DonorResponseDto dto = new DonorResponseDto();
        dto.setId(donor.getId());
        dto.setEmail(donor.getEmail());
        dto.setFullName(donor.getFullName());
        dto.setBloodGroup(donor.getBloodGroup());
        dto.setLastDonationDate(donor.getLastDonationDate());
        dto.setContactNumber(donor.getContactNumber());
        dto.setAddress(donor.getAddress());

        // Convert Medical History Entities to List<String>
        if (donor.getMedicalHistoryList() != null) {
            List<String> history = donor.getMedicalHistoryList().stream()
                    .map(MedicalHistory::getConditionName)
                    .collect(Collectors.toList());
            dto.setMedicalHistory(history);
        }

        dto.setEligible(isEligible(donor.getId())); // Recalculate eligibility on the fly
        return dto;
    }

    // Note: updateAfterDonation isn't strictly needed here
    // because DonationService.completeDonation() handles it,
    // but you can keep it if you want specific logic.
    @Override
    public void updateAfterDonation(Long donorId) {
        User donor = getDonorEntity(donorId);
        donor.setLastDonationDate(LocalDate.now());
        userRepository.save(donor);
    }
}