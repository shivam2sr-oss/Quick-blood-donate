package com.cdac.QBD.service;

import com.cdac.QBD.entity.Alert;

/**
 * NotificationService
 *
 * Responsible for delivering notifications
 * when critical system events occur.
 */
public interface NotificationService {

    /**
     * Notify all stakeholders about a newly raised alert.
     */
    void notifyAlert(Alert alert, String city);
}
