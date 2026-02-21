package com.cdac.QBD.service;

import com.cdac.QBD.entity.BloodTransfer;
import com.cdac.QBD.entity.Organization;
import com.cdac.QBD.utils.constant.BloodGroup;

import java.util.List;

/**
 * BloodTransferService
 *
 * This service handles physical movement of blood
 * between organizations.
 *
 * Examples:
 * - Node → CBB
 * - CBB → CBB (district/state escalation)
 *
 * It also ensures:
 * - Inventory is updated correctly
 * - Transfers are auditable
 */
public interface BloodTransferService {

    /**
     * Create a new blood transfer.
     *
     * This method represents DISPATCH of blood.
     */
    BloodTransfer dispatchTransfer(
            Organization fromOrg,
            Organization toOrg,
            BloodGroup bloodGroup,
            int quantity,
            String transferType
    );

    /**
     * Mark an existing transfer as RECEIVED.
     *
     * This represents physical receipt of blood.
     */
    void receiveTransfer(Long transferId);

    /**
     * Fetch all transfers where the given organization
     * is either sender or receiver.
     *
     * Used for dashboards and history views.
     */
    List<BloodTransfer> getTransfersForOrganization(Organization organization);
}

