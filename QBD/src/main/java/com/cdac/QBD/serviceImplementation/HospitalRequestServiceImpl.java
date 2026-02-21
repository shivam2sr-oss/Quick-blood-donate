package com.cdac.QBD.serviceImplementation;

import com.cdac.QBD.dto.HospitalRequestCreateDto;
import com.cdac.QBD.dto.HospitalRequestResponseDto;
import com.cdac.QBD.entity.Alert;
import com.cdac.QBD.entity.HospitalRequest;
import com.cdac.QBD.entity.Organization;
import com.cdac.QBD.repository.AlertRepository;
import com.cdac.QBD.repository.HospitalRequestRepository;
import com.cdac.QBD.repository.OrganizationRepository;
import com.cdac.QBD.service.HospitalRequestService;
import com.cdac.QBD.utils.constant.OrganizationType;
import com.cdac.QBD.utils.constant.RequestStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Hospital Request Service Implementation
 * 
 * CORE FUNCTIONALITY - Heart of the Hospital module
 * 
 * Key Principles:
 * 1. Hospital can only CREATE requests (status: PENDING)
 * 2. Hospital CANNOT update inventory
 * 3. Hospital CANNOT create transfers
 * 4. Hospital CANNOT move request status forward - only CBB can
 * 5. Request lifecycle: PENDING → DISPATCHED → DELIVERED (managed by CBB)
 * 6. Optional: PENDING → REJECTED (if CBB cannot fulfill)
 */
@Service
@Transactional
public class HospitalRequestServiceImpl implements HospitalRequestService {

    private final HospitalRequestRepository hospitalRequestRepository;
    private final OrganizationRepository organizationRepository;
    private final AlertRepository alertRepository;
    // 12-hour SLA in hours
    private static final long SLA_HOURS = 12;

    public HospitalRequestServiceImpl(
            HospitalRequestRepository hospitalRequestRepository,
            OrganizationRepository organizationRepository, AlertRepository alertRepository) {
        this.hospitalRequestRepository = hospitalRequestRepository;
        this.organizationRepository = organizationRepository;
        this.alertRepository = alertRepository;
    }

    @Override
    public HospitalRequestResponseDto createBloodRequest(HospitalRequestCreateDto dto) {
        // Validate hospital exists
        Organization hospital = organizationRepository.findById(dto.getHospitalId())
                .orElseThrow(() -> new RuntimeException("Hospital not found with ID: " + dto.getHospitalId()));

        if (hospital.getType() != OrganizationType.HOSPITAL) {
            throw new RuntimeException("Organization must be a HOSPITAL");
        }

        // Get the CBB (either from DTO or from hospital's parent)
        Organization cbb;
        if (dto.getCbbId() != null) {
            cbb = organizationRepository.findById(dto.getCbbId())
                    .orElseThrow(() -> new RuntimeException("CBB not found with ID: " + dto.getCbbId()));
        } else {
            // Auto-assign from hospital's parent organization
            cbb = hospital.getParentOrganization();
            if (cbb == null) {
                throw new RuntimeException("Hospital must be connected to a CBB");
            }
        }

        if (cbb.getType() != OrganizationType.CBB) {
            throw new RuntimeException("Target organization must be a CBB");
        }

        // Create the request
        HospitalRequest request = new HospitalRequest();
        request.setHospital(hospital);
        request.setCbb(cbb);
        request.setBloodGroup(dto.getBloodGroup());
        request.setUnitsNeeded(dto.getUnitsNeeded());
        request.setUrgency(dto.getUrgency());
        request.setStatus(RequestStatus.PENDING); // Always starts as PENDING
        request.setRequestDate(LocalDateTime.now());

        HospitalRequest savedRequest = hospitalRequestRepository.save(request);
        Alert alert = new Alert();
        alert.setBloodGroup(dto.getBloodGroup());
        alert.setResolved(false);
        alert.setUrgency(dto.getUrgency());
        alert.setHospitalRequest(savedRequest);
        alert.setRaisingOrganization(hospital);
        alert.setTargetDistrict(hospital.getDistrict());
//        alert.setEscalationLevel(0);
        alertRepository.save(alert);
        return mapToResponseDto(savedRequest);
    }

    @Override
    public HospitalRequestResponseDto getRequestStatus(Long requestId) {
        HospitalRequest request = getRequestById(requestId);
        return mapToResponseDto(request);
    }

    @Override
    public List<HospitalRequestResponseDto> getAllRequestsByHospital(Long hospitalId) {
        // Validate hospital exists
        Organization hospital = organizationRepository.findById(hospitalId)
                .orElseThrow(() -> new RuntimeException("Hospital not found with ID: " + hospitalId));

        if (hospital.getType() != OrganizationType.HOSPITAL) {
            throw new RuntimeException("Organization must be a HOSPITAL");
        }

        List<HospitalRequest> requests = hospitalRequestRepository.findByHospitalId(hospitalId);
        
        return requests.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<HospitalRequestResponseDto> getPendingRequestsByHospital(Long hospitalId) {
        List<HospitalRequest> requests = hospitalRequestRepository.findByHospitalIdAndStatus(
                hospitalId, RequestStatus.PENDING);
        
        return requests.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<HospitalRequestResponseDto> getDeliveredRequestsByHospital(Long hospitalId) {
        List<HospitalRequest> requests = hospitalRequestRepository.findByHospitalIdAndStatus(
                hospitalId, RequestStatus.DELIVERED);
        
        return requests.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public HospitalRequest getRequestById(Long requestId) {
        return hospitalRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Hospital request not found with ID: " + requestId));
    }

    @Override
    public boolean isSlaBreach(Long requestId) {
        HospitalRequest request = getRequestById(requestId);
        
        if (request.getStatus() == RequestStatus.DELIVERED) {
            return false; // Already delivered, no breach
        }

        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(request.getRequestDate(), now);
        long hoursElapsed = duration.toHours();

        return hoursElapsed > SLA_HOURS;
    }

    /**
     * Helper method to map HospitalRequest entity to HospitalRequestResponseDto
     */
    private HospitalRequestResponseDto mapToResponseDto(HospitalRequest request) {
        HospitalRequestResponseDto dto = new HospitalRequestResponseDto();
        
        dto.setId(request.getId());
        dto.setHospitalId(request.getHospital().getId());
        dto.setHospitalName(request.getHospital().getName());
        dto.setCbbId(request.getCbb().getId());
        dto.setCbbName(request.getCbb().getName());
        dto.setBloodGroup(request.getBloodGroup());
        dto.setUnitsNeeded(request.getUnitsNeeded());
        dto.setUrgency(request.getUrgency());
        dto.setStatus(request.getStatus());
        dto.setRequestDate(request.getRequestDate());

        // Calculate hours elapsed
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(request.getRequestDate(), now);
        long hoursElapsed = duration.toHours();
        dto.setHoursElapsed(hoursElapsed);

        // Check SLA breach (only for pending/dispatched requests)
        if (request.getStatus() != RequestStatus.DELIVERED && 
            request.getStatus() != RequestStatus.REJECTED) {
            dto.setSlaBreached(hoursElapsed > SLA_HOURS);
        } else {
            dto.setSlaBreached(false);
        }

        return dto;
    }
}
