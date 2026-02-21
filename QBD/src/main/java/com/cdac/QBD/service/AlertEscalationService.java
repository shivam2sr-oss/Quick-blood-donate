package com.cdac.QBD.service;

/**
 * AlertEscalationService
 *
 * CONTRACT:
 * ----------
 * Defines escalation behavior for unresolved alerts.
 *
 * Implementations must:
 * - Detect unresolved alerts
 * - Escalate based on time & urgency
 * - Trigger notifications
 */
public interface AlertEscalationService {

    /**
     * Periodic escalation check.
     *
     * This method is expected to be:
     * - Scheduled
     * - Fully automated
     */
    void checkAndEscalateAlerts();
}
