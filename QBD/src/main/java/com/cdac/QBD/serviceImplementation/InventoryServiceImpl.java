package com.cdac.QBD.serviceImplementation;

import com.cdac.QBD.entity.Alert;
import com.cdac.QBD.entity.BloodInventory;
import com.cdac.QBD.entity.Organization;
import com.cdac.QBD.repository.AlertRepository;
import com.cdac.QBD.repository.BloodInventoryRepository;
import com.cdac.QBD.service.InventoryService;
import com.cdac.QBD.service.NotificationService;
import com.cdac.QBD.service.OrganizationService;
import com.cdac.QBD.utils.constant.BloodGroup;
import com.cdac.QBD.utils.constant.OrganizationType;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * InventoryServiceImpl
 *
 * CORE BUSINESS LOGIC for blood inventory management.
 *
 * RULES:
 * - Inventory exists ONLY for CBB
 * - Quantity must always be positive
 * - No negative stock allowed
 * - Low stock automatically raises alerts
 * - Alerts automatically trigger notifications
 */
@Service
public class InventoryServiceImpl implements InventoryService {

    private final BloodInventoryRepository inventoryRepository;
    private final AlertRepository alertRepository;
    private final NotificationService notificationService;

    /**
     * TEMP threshold (configurable later)
     */
    private static final int THRESHOLD_UNITS = 10;

    /**
     * Constructor Injection
     */
    public InventoryServiceImpl(BloodInventoryRepository inventoryRepository,
            AlertRepository alertRepository,
            NotificationService notificationService) {
        this.inventoryRepository = inventoryRepository;
        this.alertRepository = alertRepository;
        this.notificationService = notificationService;
    }

    /**
     * Fetch inventory for a specific blood group of a CBB.
     *
     * If inventory does not exist, it is created with quantity = 0.
     */
    @Override
    public BloodInventory getInventory(Organization cbb, BloodGroup bloodGroup) {
        validateCBB(cbb);
        return inventoryRepository.findAll()
                .stream()
                .filter(inv -> inv.getOrganization().getId().equals(cbb.getId())
                        && inv.getBloodGroup() == bloodGroup)
                .findFirst()
                .orElseGet(() -> createInventory(cbb, bloodGroup));
    }

    /**
     * Add stock to inventory.
     *
     * Used when:
     * - Node sends blood to CBB
     * - Another CBB transfers blood
     */
    @Override
    public void addStock(Organization cbb, BloodGroup bloodGroup, int quantity, String source) {
        validateCBB(cbb);
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity to add must be greater than zero");
        }

        BloodInventory inventory = getInventory(cbb, bloodGroup);
        inventory.setQuantity(inventory.getQuantity() + quantity);
        inventoryRepository.save(inventory);
    }

    /**
     * Deduct stock from inventory.
     *
     * Used when:
     * - Hospital request is fulfilled
     * - Blood is transferred to another CBB
     */
    @Override
    public void deductStock(Organization cbb, BloodGroup bloodGroup, int quantity, String reason) {
        validateCBB(cbb);
        // ← REMOVED: System.out.println("valid");

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity to deduct must be greater than zero");
        }

        BloodInventory inventory = getInventory(cbb, bloodGroup);
        if (inventory.getQuantity() < quantity) {
            throw new IllegalArgumentException(
                    "Insufficient blood stock for " + bloodGroup);
        }

        inventory.setQuantity(inventory.getQuantity() - quantity);
        inventoryRepository.save(inventory);

        // After deduction → check threshold
        checkAndTriggerAlert(cbb, bloodGroup);
    }

    /**
     * Trigger alert + notifications if stock goes below threshold.
     * Only creates a new alert if no unresolved alert exists for this CBB + blood
     * group.
     */
    @Override
    public void checkAndTriggerAlert(Organization cbb, BloodGroup bloodGroup) {
        BloodInventory inventory = getInventory(cbb, bloodGroup);

        if (inventory.getQuantity() < THRESHOLD_UNITS) {
            // Check if an unresolved alert already exists for this CBB + blood group
            boolean unresolvedAlertExists = alertRepository.findAll()
                    .stream()
                    .anyMatch(alert -> alert.getRaisingOrganization().getId().equals(cbb.getId()) &&
                            alert.getBloodGroup() == bloodGroup &&
                            !alert.isResolved());

            // Only create new alert if no unresolved alert exists
            if (!unresolvedAlertExists) {
                Alert alert = new Alert();
                alert.setRaisingOrganization(cbb);
                alert.setBloodGroup(bloodGroup);
                alert.setTargetDistrict(cbb.getDistrict());
                alert.setMessage(
                        "Low stock alert: " + bloodGroup + " at CBB " + cbb.getCity());
                alert.setResolved(false);
                alert.setCreatedAt(LocalDateTime.now());

                // Save alert
                Alert savedAlert = alertRepository.save(alert);

                // 🔔 Trigger notifications — pass city directly from cbb
                notificationService.notifyAlert(savedAlert, cbb.getCity());
            }
            // If unresolved alert exists, skip creating a new one (prevents duplicate
            // emails)
        }
    }

    /**
     * Fetch complete inventory of a CBB.
     */
    @Override
    public List<BloodInventory> getInventoryByOrganization(Organization cbb) {
        validateCBB(cbb);
        return inventoryRepository.findByOrganization(cbb);
    }

    // ---------------- PRIVATE HELPERS ----------------

    /**
     * Ensure only CBB can access inventory operations.
     */
    private void validateCBB(Organization organization) {
        if (organization.getType() != OrganizationType.CBB) {
            throw new IllegalArgumentException(
                    "Inventory operations are allowed only for CBB");
        }
    }

    /**
     * Create inventory row for a blood group if it doesn't exist.
     */
    private BloodInventory createInventory(Organization cbb, BloodGroup bloodGroup) {
        BloodInventory inventory = new BloodInventory();
        inventory.setOrganization(cbb);
        inventory.setBloodGroup(bloodGroup);
        inventory.setQuantity(0);
        return inventoryRepository.save(inventory);
    }
}