package com.cdac.QBD.controller;

import com.cdac.QBD.dto.BloodTransferDispatchDto;
import com.cdac.QBD.entity.BloodTransfer;
import com.cdac.QBD.entity.Organization;
import com.cdac.QBD.service.BloodTransferService;
import com.cdac.QBD.service.OrganizationService;
import com.cdac.QBD.utils.constant.BloodGroup;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blood-transfer")
public class BloodTransferController {

    private final BloodTransferService bloodTransferService;
    private final OrganizationService organizationService;

    /**
     * Constructor Injection
     */
    public BloodTransferController(BloodTransferService bloodTransferService,
                                   OrganizationService organizationService) {
        this.bloodTransferService = bloodTransferService;
        this.organizationService = organizationService;
    }

    /**
     * DISPATCH BLOOD
     *
     * Used when:
     * - Node sends collected blood to CBB
     * - One CBB sends blood to another CBB
     *
     * FLOW:
     * 1. Validate fromOrg & toOrg
     * 2. Create BloodTransfer entry
     * 3. Deduct inventory from sender (if applicable)
     * 4. Mark transfer as DISPATCHED
     */
//    @PostMapping("/dispatch")
//    public ResponseEntity<String> dispatchBlood(
//            @RequestParam Long fromOrgId,
//            @RequestParam Long toOrgId,
//            @RequestParam BloodGroup bloodGroup,
//            @RequestParam int quantity,
//            @RequestParam String transferType
//    ) {
//
//        Organization fromOrg = organizationService.getOrganizationById(fromOrgId);
//        Organization toOrg = organizationService.getOrganizationById(toOrgId);
//
//        bloodTransferService.dispatchTransfer(
//                fromOrg,
//                toOrg,
//                bloodGroup,
//                quantity,
//                transferType
//        );
//
//        return ResponseEntity.ok("Blood transfer dispatched successfully");
//    }

    @PostMapping("/dispatch")
    public ResponseEntity<String> dispatchBlood(@RequestBody BloodTransferDispatchDto dto) {
        Organization fromOrg = organizationService.getOrganizationById(dto.getFromOrgId());
        Organization toOrg = organizationService.getOrganizationById(dto.getToOrgId());

        bloodTransferService.dispatchTransfer(
                fromOrg,
                toOrg,
                dto.getBloodGroup(),
                dto.getQuantity(),
                dto.getTransferType()
        );
        return ResponseEntity.ok("Blood transfer dispatched successfully");
    }

    /**
     * RECEIVE BLOOD
     *
     * Called when:
     * - CBB receives blood from Node
     * - CBB receives blood from another CBB
     *
     * FLOW:
     * 1. Fetch transfer record
     * 2. Add stock to receiver inventory
     * 3. Update transfer status to RECEIVED
     */
    @PostMapping("/receive/{transferId}")
    public ResponseEntity<String> receiveBlood(
            @PathVariable Long transferId
    ) {

        bloodTransferService.receiveTransfer(transferId);

        return ResponseEntity.ok("Blood transfer received successfully");
    }

    /**
     * VIEW TRANSFER HISTORY
     *
     * Used by:
     * - CBB dashboard
     * - Admin view
     *
     * Returns all transfers where organization
     * is either sender or receiver.
     */
    @GetMapping("/history/{orgId}")
    public ResponseEntity<List<BloodTransfer>> getTransferHistory(
            @PathVariable Long orgId
    ) {

        Organization organization = organizationService.getOrganizationById(orgId);

        List<BloodTransfer> transfers =
                bloodTransferService.getTransfersForOrganization(organization);

        return ResponseEntity.ok(transfers);
    }
}

