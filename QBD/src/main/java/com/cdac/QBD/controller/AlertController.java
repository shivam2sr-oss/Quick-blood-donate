package com.cdac.QBD.controller;

import com.cdac.QBD.entity.Alert;
import com.cdac.QBD.service.AlertService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AlertController
 *
 * This controller provides READ access to alert data.
 *
 * IMPORTANT DESIGN DECISION:
 * - Alerts are NOT created via API
 * - Alerts are generated automatically by InventoryService
 * - Controller is responsible only for:
 *     - Viewing alerts
 *     - Resolving alerts (delegates to AlertService)
 */
@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    /**
     * GET ALL ALERTS
     *
     * Used by:
     * - Admin dashboard
     * - CBB dashboard
     *
     * Returns:
     * - Both resolved and unresolved alerts
     */
    @GetMapping
    public ResponseEntity<List<Alert>> getAllAlerts() {
        return ResponseEntity.ok(alertService.getAllAlerts());
    }

    /**
     * GET UNRESOLVED ALERTS
     *
     * Used to show:
     * - Active shortages
     * - Alerts that still need action
     */
    @GetMapping("/unresolved")
    public ResponseEntity<List<Alert>> getUnresolvedAlerts() {
        return ResponseEntity.ok(alertService.getUnresolvedAlerts());
    }

    /**
     * RESOLVE ALERT
     *
     * Called when:
     * - Stock is replenished
     * - Manual verification done
     *
     * All business logic (updating hospital request,
     * deducting stock, dispatching transfer) is handled
     * inside AlertService.
     */
    @PostMapping("/resolve/{alertId}")
    public ResponseEntity<String> resolveAlert(@PathVariable Long alertId) {
        alertService.resolveAlert(alertId);
        return ResponseEntity.ok("Alert resolved successfully");
    }
}