package com.cdac.QBD.serviceImplementation;

import com.cdac.QBD.entity.Alert;
import com.cdac.QBD.entity.Organization;
import com.cdac.QBD.entity.User;
import com.cdac.QBD.repository.AlertRepository;
import com.cdac.QBD.repository.OrganizationRepository;
import com.cdac.QBD.repository.UserRepository;
import com.cdac.QBD.service.AlertEscalationService;
import com.cdac.QBD.service.EmailService;
import com.cdac.QBD.utils.constant.OrganizationType;
import com.cdac.QBD.utils.constant.UrgencyLevel;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * AlertEscalationServiceImpl
 *
 * RESPONSIBILITY:
 * -------------------------
 * - Automatically escalates unresolved alerts
 * - Escalation levels:
 *      0 → City CBB
 *      1 → District CBB
 *      2 → State CBB
 *
 * - Escalation depends on:
 *      - Time elapsed
 *      - Urgency level
 */
@Service
public class AlertEscalationServiceImpl implements AlertEscalationService {

    private final AlertRepository alertRepository;
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;          // ← ADDED: needed to look up email
    private final EmailService emailService;

    public AlertEscalationServiceImpl(AlertRepository alertRepository,
                                      OrganizationRepository organizationRepository,
                                      UserRepository userRepository,
                                      EmailService emailService) {
        this.alertRepository = alertRepository;
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;             // ← ADDED
        this.emailService = emailService;
    }

    /**
     * Scheduler runs every 15 minutes.
     *
     * This method is AUTOMATIC and requires no controller.
     */
    @Override
    @Scheduled(fixedRate = 15 * 60 * 1000)
    public void checkAndEscalateAlerts() {

        List<Alert> activeAlerts = alertRepository.findAll()
                .stream()
                .filter(alert -> !alert.isResolved())
                .toList();

        for (Alert alert : activeAlerts) {

            Duration duration = Duration.between(
                    alert.getCreatedAt(),
                    LocalDateTime.now()
            );

            // 🚨 CRITICAL → immediate district escalation
            if (alert.getUrgency() == UrgencyLevel.CRITICAL
                    && alert.getEscalationLevel() == 0) {

                notifyDistrictCBBs(alert);
                alert.setEscalationLevel(1);
                alertRepository.save(alert);
                continue;
            }

            // ⏱️ 12 hours → district escalation
            if (duration.toHours() >= 12 && alert.getEscalationLevel() == 0) {

                notifyDistrictCBBs(alert);
                alert.setEscalationLevel(1);
                alertRepository.save(alert);
                continue;
            }

            // ⏱️ 24 hours → state escalation
            if (duration.toHours() >= 24 && alert.getEscalationLevel() == 1) {

                notifyStateCBBs(alert);
                alert.setEscalationLevel(2);
                alertRepository.save(alert);
            }
        }
    }

    // ---------------- PRIVATE HELPERS ----------------

    private void notifyDistrictCBBs(Alert alert) {

        Long raisingOrgId = alert.getRaisingOrganization().getId();  // ← ADDED: to exclude self

        List<Organization> districtCBBs = organizationRepository.findAll()
                .stream()
                .filter(org ->
                        org.getType() == OrganizationType.CBB &&
                                !org.getId().equals(raisingOrgId) &&          // ← ADDED: exclude self
                                org.getDistrict().equalsIgnoreCase(
                                        alert.getRaisingOrganization().getDistrict()
                                )
                )
                .toList();

        for (Organization cbb : districtCBBs) {
            User cbbUser = userRepository.findByOrganization_Id(cbb.getId());  // ← CHANGED: look up user
            if (cbbUser != null && cbbUser.getEmail() != null) {               // ← ADDED: null check
                emailService.sendEmail(
                        cbbUser.getEmail(),                                    // ← CHANGED: was cbb.getContactNumber()
                        "LOW BLOOD STOCK ALERT – DISTRICT LEVEL",
                        alert.getMessage()
                );
            }
        }
    }

    private void notifyStateCBBs(Alert alert) {

        Long raisingOrgId = alert.getRaisingOrganization().getId();  // ← ADDED: to exclude self

        List<Organization> stateCBBs = organizationRepository.findAll()
                .stream()
                .filter(org ->
                        org.getType() == OrganizationType.CBB &&
                                !org.getId().equals(raisingOrgId) &&          // ← ADDED: exclude self
                                org.getState().equalsIgnoreCase(
                                        alert.getRaisingOrganization().getState()
                                )
                )
                .toList();

        for (Organization cbb : stateCBBs) {
            User cbbUser = userRepository.findByOrganization_Id(cbb.getId());  // ← CHANGED: look up user
            if (cbbUser != null && cbbUser.getEmail() != null) {               // ← ADDED: null check
                emailService.sendEmail(
                        cbbUser.getEmail(),                                    // ← CHANGED: was cbb.getContactNumber()
                        "LOW BLOOD STOCK ALERT – STATE LEVEL",
                        alert.getMessage()
                );
            }
        }
    }
}