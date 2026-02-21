package com.cdac.QBD.serviceImplementation;

import com.cdac.QBD.entity.Alert;
import com.cdac.QBD.entity.Organization;
import com.cdac.QBD.entity.User;
import com.cdac.QBD.repository.OrganizationRepository;
import com.cdac.QBD.repository.UserRepository;
import com.cdac.QBD.service.EmailService;
import com.cdac.QBD.service.NotificationService;
import com.cdac.QBD.utils.constant.OrganizationType;
import com.cdac.QBD.utils.constant.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * NotificationServiceImpl
 *
 * Sends INDIVIDUAL emails for each alert.
 * City-based filtering is applied — notifications go to
 * the same CITY as the CBB that raised the alert.
 */
@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public NotificationServiceImpl(OrganizationRepository organizationRepository,
            UserRepository userRepository,
            EmailService emailService) {
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    /**
     * Entry point: called from InventoryService when stock drops below threshold.
     *
     * @param alert the saved Alert entity
     * @param city  the CITY of the CBB that raised the alert
     */
    @Override
    public void notifyAlert(Alert alert, String city) {
        logger.info("🔔 Triggering notifications for alert: {} | Blood Group: {} | City: {}",
                alert.getId(), alert.getBloodGroup(), city);

        notifyNearbyCBBs(alert, city);
        notifyNodeStaff(alert, city);
        notifyEligibleDonors(alert, city);

        logger.info("✅ Notification process completed for alert: {}", alert.getId());
    }

    /**
     * Notify other CBBs in the SAME CITY.
     * ← FIXED: was filtering by district, now filters by city
     * ← FIXED: excludes the CBB that raised the alert (no self-notification)
     * ← FIXED: added null check on cbbUser
     */
    private void notifyNearbyCBBs(Alert alert, String city) {

        Long raisingOrgId = alert.getRaisingOrganization().getId();

        List<Organization> cbbs = organizationRepository.findAll()
                .stream()
                .filter(org -> org.getType() == OrganizationType.CBB &&
                        !org.getId().equals(raisingOrgId) && // exclude self
                        city.equalsIgnoreCase(org.getCity()) // match by CITY
                )
                .toList();

        logger.info("📧 Notifying {} CBB(s) in city: {}", cbbs.size(), city);

        for (Organization cbb : cbbs) {
            User cbbUser = userRepository.findByOrganization_Id(cbb.getId());
            if (cbbUser != null && cbbUser.getEmail() != null) {
                emailService.sendEmail(
                        cbbUser.getEmail(),
                        "🚨 Low Blood Stock Alert",
                        alert.getMessage());
            }
        }
    }

    /**
     * Notify Node staff in the SAME CITY to arrange donation camps.
     * ← FIXED: was filtering by district, now filters by city
     */
    private void notifyNodeStaff(Alert alert, String city) {

        List<User> nodeStaff = userRepository.findAll()
                .stream()
                .filter(user -> user.getRole() == UserRole.NODE_STAFF &&
                        user.getOrganization() != null &&
                        city.equalsIgnoreCase(user.getOrganization().getCity()) // match by CITY
                )
                .toList();

        logger.info("📧 Notifying {} Node Staff member(s) in city: {}", nodeStaff.size(), city);

        for (User staff : nodeStaff) {
            emailService.sendEmail(
                    staff.getEmail(),
                    "🩸 Action Required: Arrange Donation Camps",
                    alert.getMessage());
        }
    }

    /**
     * Notify eligible DONORS in the SAME CITY as the CBB.
     * ← FIXED: was using address.contains(district), now uses user.getCity()
     * ← FIXED: added user.isEligible() check (90-day cooldown)
     * ← FIXED: removed unnecessary org == null check (donors can have city without
     * org)
     */
    private void notifyEligibleDonors(Alert alert, String city) {

        List<User> donors = userRepository.findAll()
                .stream()
                .filter(user -> user.getRole() == UserRole.DONOR &&
                        user.isEligible() && // only 90-day eligible donors
                        user.getCity() != null &&
                        city.equalsIgnoreCase(user.getCity()) // match by CITY
                )
                .toList();

        logger.info("📧 Notifying {} eligible donor(s) in city: {}", donors.size(), city);

        for (User donor : donors) {
            emailService.sendEmail(
                    donor.getEmail(),
                    "🙏 Urgent Blood Requirement",
                    alert.getMessage());
        }
    }
}