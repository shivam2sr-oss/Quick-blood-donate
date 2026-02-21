package com.cdac.QBD.service;

import com.cdac.QBD.entity.Alert;

import java.util.List;

/**
 * AlertService
 *
 * PURPOSE:
 * --------
 * This service manages ALERTS generated in the system.
 *
 * Alerts are created when:
 * - Blood inventory goes below threshold
 *
 * This service is responsible for:
 * - Viewing alerts
 * - Filtering unresolved alerts
 * - Resolving alerts
 *
 *
 * -------------------------------------------------
 * Alert creation logic already exists in InventoryService.
 * This service only MANAGES alerts after creation.
 */
public interface AlertService {

    /**
     * Fetch all alerts in the system.
     *
     * Used by:
     * - Admin dashboard
     * - CBB monitoring screens
     */
    List<Alert> getAllAlerts();

    /**
     * Fetch only unresolved (active) alerts.
     *
     * Used by:
     * - CBB staff to identify current shortages
     * - Escalation workflows
     */
    List<Alert> getUnresolvedAlerts();

    List<Alert> getUnresolvedAlertsByCity(String city);

    /**
     * Mark an alert as resolved.
     *
     * This is typically done when:
     * - Blood stock is replenished
     * - Situation is handled manually
     */
    void resolveAlert(Long alertId);

//    public List<Alert> getUnresolvedAlertsByCity(String city);
}

