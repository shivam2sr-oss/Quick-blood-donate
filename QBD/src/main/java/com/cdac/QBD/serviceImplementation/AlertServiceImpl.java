package com.cdac.QBD.serviceImplementation;

import com.cdac.QBD.entity.Alert;
import com.cdac.QBD.entity.BloodInventory;
import com.cdac.QBD.entity.HospitalRequest;
import com.cdac.QBD.entity.Organization;
import com.cdac.QBD.repository.AlertRepository;
import com.cdac.QBD.repository.HospitalRequestRepository;
import com.cdac.QBD.service.AlertService;
import com.cdac.QBD.service.BloodTransferService;
import com.cdac.QBD.service.InventoryService;
import com.cdac.QBD.utils.constant.RequestStatus;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * AlertServiceImpl
 *
 * RESPONSIBILITY:
 * ----------------
 * - Manage alerts AFTER they are created
 * - Fetch alerts for monitoring
 * - Resolve alerts once handled (includes fulfilling hospital request)
 *
 * IMPORTANT:
 * -----------
 * Alert CREATION happens in InventoryService
 * Alert NOTIFICATION handled separately (EmailService)
 */
@Service
public class AlertServiceImpl implements AlertService {

    private final AlertRepository alertRepository;
    private final HospitalRequestRepository hospitalRequestRepository;
    private final InventoryService inventoryService;
    private final BloodTransferService bloodTransferService;

    public AlertServiceImpl(AlertRepository alertRepository,
                            HospitalRequestRepository hospitalRequestRepository,
                            InventoryService inventoryService,
                            BloodTransferService bloodTransferService) {
        this.alertRepository = alertRepository;
        this.hospitalRequestRepository = hospitalRequestRepository;
        this.inventoryService = inventoryService;
        this.bloodTransferService = bloodTransferService;
    }

    /**
     * Fetch all alerts (resolved + unresolved).
     *
     * Used by:
     * - Admin dashboards
     * - Audit / history views
     */
    @Override
    public List<Alert> getAllAlerts() {
        return alertRepository.findAll();
    }

    /**
     * Fetch only unresolved alerts.
     *
     * Used by:
     * - CBB staff monitoring shortages
     */
    @Override
    public List<Alert> getUnresolvedAlerts() {
        return alertRepository.findAll()
                .stream()
                .filter(alert -> !alert.isResolved())
                .toList();
    }

    /**
     * Fetch unresolved alerts for a specific CITY.
     *
     * CORE BUSINESS RULE (CURRENT):
     * -----------------------------
     * - Alerts are handled at CITY-level CBBs
     * - District / State escalation NOT implemented yet
     */
    @Override
    public List<Alert> getUnresolvedAlertsByCity(String city) {
        return alertRepository.findAll()
                .stream()
                .filter(alert ->
                        !alert.isResolved()
                                && alert.getRaisingOrganization() != null
                                && city.equalsIgnoreCase(
                                alert.getRaisingOrganization().getCity()
                        )
                )
                .toList();
    }

    /**
     * Resolve an alert.
     *
     * Full flow:
     * 1. Mark alert as resolved
     * 2. Approve the linked hospital request (if exists)
     * 3. Deduct stock from the parent CBB
     * 4. Dispatch blood transfer from CBB to hospital
     *
     * NOTE:
     * -----
     * Alerts are NOT deleted.
     * They are kept for audit and traceability.
     */
    @Override
    public void resolveAlert(Long alertId) {

        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Alert not found with id: " + alertId
                        )
                );

        // 1. Mark alert resolved
        alert.setResolved(true);
        alertRepository.save(alert);

        // 2. If a hospital request is linked, approve it and fulfill
        HospitalRequest hospitalRequest = alert.getHospitalRequest();
        if (hospitalRequest != null) {
            hospitalRequest.setStatus(RequestStatus.APPROVED);
            HospitalRequest savedHospitalRequest = hospitalRequestRepository.save(hospitalRequest);

            // 3. Deduct stock from the CBB (parent of the hospital)
            Organization cbb = alert.getRaisingOrganization().getParentOrganization();
            inventoryService.deductStock(
                    cbb,
                    alert.getBloodGroup(),
                    savedHospitalRequest.getUnitsNeeded(),
                    "Fulfilled hospital request ID: " + savedHospitalRequest.getId()
            );

            // 4. Dispatch transfer from CBB to hospital
//            bloodTransferService.dispatchTransfer(
//                    cbb,
//                    alert.getRaisingOrganization(),
//                    alert.getBloodGroup(),
//                    savedHospitalRequest.getUnitsNeeded(),
//                    "Hospital Request Fulfillment"
//            );
        }
    }
}