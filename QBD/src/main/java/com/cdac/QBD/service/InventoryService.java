package com.cdac.QBD.service;

import java.util.List;

import com.cdac.QBD.entity.BloodInventory;
import com.cdac.QBD.entity.Organization;
import com.cdac.QBD.utils.constant.BloodGroup;

/**
 * InventoryService
 *
 * This service is the SINGLE AUTHORITY for managing
 * blood stock at Central Blood Banks (CBB).
 *
 * No other service should directly update BloodInventory.
 */
public interface InventoryService {

    /**
     * Fetch inventory record for a given CBB and blood group.
     * If inventory does not exist, it should be created with quantity = 0.
     */
    BloodInventory getInventory(Organization cbb, BloodGroup bloodGroup);

    /**
     * Add blood units to inventory.
     * Used when blood is received from:
     * - Node
     * - Another CBB
     */
    void addStock(Organization cbb, BloodGroup bloodGroup, int quantity, String source);

    /**
     * Deduct blood units from inventory.
     * Used when blood is:
     * - Given to hospital
     * - Transferred to another CBB
     */
    void deductStock(Organization cbb, BloodGroup bloodGroup, int quantity, String reason);

    /**
     * Check if inventory is below threshold and trigger alert.
     */
    void checkAndTriggerAlert(Organization cbb, BloodGroup bloodGroup);

    //
    /**
     * Fetch complete inventory for a given CBB.
     *
     * Used by dashboards and controllers.
     */
    List<BloodInventory> getInventoryByOrganization(Organization cbb);
}
