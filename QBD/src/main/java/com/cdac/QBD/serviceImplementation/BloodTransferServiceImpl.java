package com.cdac.QBD.serviceImplementation;

import com.cdac.QBD.entity.BloodTransfer;
import com.cdac.QBD.entity.Organization;
import com.cdac.QBD.repository.BloodTransferRepository;
import com.cdac.QBD.service.BloodTransferService;
import com.cdac.QBD.service.InventoryService;
import com.cdac.QBD.utils.constant.BloodGroup;
import com.cdac.QBD.utils.constant.OrganizationType;
import com.cdac.QBD.utils.constant.RequestStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * BloodTransferServiceImpl
 *
 * Handles business logic for blood movement
 * between organizations.
 */
@Service
public class BloodTransferServiceImpl implements BloodTransferService {

    private final BloodTransferRepository bloodTransferRepository;
    private final InventoryService inventoryService;

    public BloodTransferServiceImpl(BloodTransferRepository bloodTransferRepository,
                                    InventoryService inventoryService) {
        this.bloodTransferRepository = bloodTransferRepository;
        this.inventoryService = inventoryService;
    }

    /**
     * Dispatch blood from one organization to another.
     *
     * @return saved BloodTransfer entity
     */
    @Override
    public BloodTransfer dispatchTransfer(
            Organization fromOrg,
            Organization toOrg,
            BloodGroup bloodGroup,
            int quantity,
            String transferType
    ) {
        System.out.println(fromOrg);
        System.out.println(toOrg);
        System.out.println(bloodGroup);
        System.out.println(quantity);
        System.out.println(transferType);

        if (quantity <= 0) {
            throw new IllegalArgumentException("Transfer quantity must be greater than zero");
        }

        // Deduct inventory only if sender is CBB
        if (fromOrg.getType() == OrganizationType.CBB) {
            inventoryService.deductStock(
                    fromOrg,
                    bloodGroup,
                    quantity,
                    "Blood transfer dispatch"
            );
        }else {
            inventoryService.addStock(toOrg,bloodGroup,quantity,"Blood transfer dispatch");
        }

        BloodTransfer transfer = new BloodTransfer();
        transfer.setFromOrg(fromOrg);
        transfer.setToOrg(toOrg);
        transfer.setBloodGroup(bloodGroup);
        transfer.setQuantity(quantity);
        transfer.setTransferType(transferType);
        transfer.setStatus(RequestStatus.DISPATCHED);
        transfer.setTransferDate(LocalDateTime.now());

        return bloodTransferRepository.save(transfer);
    }

    /**
     * Receive (deliver) blood at destination organization.
     */
    @Override
    public void receiveTransfer(Long transferId) {

        BloodTransfer transfer = bloodTransferRepository.findById(transferId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Blood transfer not found with id: " + transferId)
                );

        if (transfer.getStatus() != RequestStatus.DISPATCHED) {
            throw new IllegalArgumentException(
                    "Only dispatched transfers can be delivered"
            );
        }

        inventoryService.addStock(
                transfer.getToOrg(),
                transfer.getBloodGroup(),
                transfer.getQuantity(),
                "Blood transfer delivered"
        );

        transfer.setStatus(RequestStatus.DELIVERED);
        bloodTransferRepository.save(transfer);
    }

    /**
     * Fetch transfer history for an organization.
     */
    @Override
    public List<BloodTransfer> getTransfersForOrganization(Organization organization) {

        return bloodTransferRepository.findAll()
                .stream()
                .filter(t ->
                        t.getFromOrg().getId().equals(organization.getId()) ||
                                t.getToOrg().getId().equals(organization.getId())
                )
                .toList();
    }
}
