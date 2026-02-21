package com.cdac.QBD.controller;

import com.cdac.QBD.dto.BloodStockUpdateDto;
import com.cdac.QBD.entity.BloodInventory;
import com.cdac.QBD.entity.Organization;
import com.cdac.QBD.repository.OrganizationRepository;
import com.cdac.QBD.service.InventoryService;
import com.cdac.QBD.utils.constant.BloodGroup;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * InventoryController
 *
 * PURPOSE:
 * --------
 * Exposes REST APIs to manage and view blood inventory
 * for Central Blood Banks (CBB).
 *
 * IMPORTANT:
 * ----------
 * - This controller does NOT contain business logic
 * - It delegates all rules to InventoryService
 * - It is frontend-facing
 */
@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;
    private final OrganizationRepository organizationRepository;

    /**
     * Constructor Injection
     */
    public InventoryController(InventoryService inventoryService,
                               OrganizationRepository organizationRepository) {
        this.inventoryService = inventoryService;
        this.organizationRepository = organizationRepository;
    }

    /**
     * GET COMPLETE INVENTORY OF A CBB
     *
     * URL:
     * ----
     * GET /inventory/{cbbId}
     *
     * USE CASE:
     * ---------
     * - Dashboard view for CBB staff
     * - Admin monitoring
     *
     * FLOW:
     * -----
     * Frontend
     *   → InventoryController
     *      → InventoryService
     *          → Database
     *              → Response
     */
    @GetMapping("/{cbbId}")
    public ResponseEntity<List<BloodInventory>> getInventory(@PathVariable Long cbbId) {

        // Step 1: Fetch CBB from DB
        Organization cbb = organizationRepository.findById(cbbId)
                .orElseThrow(() ->
                        new IllegalArgumentException("CBB not found with id: " + cbbId)
                );

        // Step 2: Fetch all inventory entries for this CBB
        List<BloodInventory> inventory =
                inventoryService.getInventoryByOrganization(cbb);

        // Step 3: Return inventory to frontend
        return ResponseEntity.ok(inventory);
    }

    /**
     * ADD BLOOD STOCK TO CBB
     *
     * URL:
     * ----
     * POST /inventory/add
     *
     * USE CASE:
     * ---------
     * - Blood received from Node
     * - Blood received from another CBB
     * - Manual stock correction
     *
     * NOTE:
     * -----
     * Low-stock alert generation is handled
     * automatically inside InventoryService.
     */
//    @PostMapping("/add")
//    public ResponseEntity<String> addStock(
//            @RequestParam Long cbbId,
//            @RequestParam BloodGroup bloodGroup,
//            @RequestParam int quantity,
//            @RequestParam(required = false) String remarks
//    ) {
//
//        Organization cbb = organizationRepository.findById(cbbId)
//                .orElseThrow(() ->
//                        new IllegalArgumentException("CBB not found with id: " + cbbId)
//                );
//
//        inventoryService.addStock(cbb, bloodGroup, quantity, remarks);
//
//        return ResponseEntity.ok("Blood stock added successfully");


    @PostMapping("/add")
    public ResponseEntity<String> addStock(@RequestBody BloodStockUpdateDto dto) {
        Organization cbb = organizationRepository.findById(dto.getCbbId())
                .orElseThrow(() -> new RuntimeException("CBB not found"));

        inventoryService.addStock(cbb, dto.getBloodGroup(), dto.getQuantity(), dto.getRemarks());
        return ResponseEntity.ok("Blood stock added successfully");
    }

    @PostMapping("/deduct")
    public ResponseEntity<String> deductStock(@RequestBody BloodStockUpdateDto dto) {
        Organization cbb = organizationRepository.findById(dto.getCbbId())
                .orElseThrow(() -> new RuntimeException("CBB not found"));

        inventoryService.deductStock(cbb, dto.getBloodGroup(), dto.getQuantity(), dto.getRemarks());
        return ResponseEntity.ok("Blood stock deducted successfully");
    }

    /**
     * DEDUCT BLOOD STOCK FROM CBB
     *
     * URL:
     * ----
     * POST /inventory/deduct
     *
     * USE CASE:
     * ---------
     * - Blood issued to hospital
     * - Emergency usage
     *
     * IMPORTANT:
     * ----------
     * - If stock goes below threshold,
     *   InventoryService automatically creates an Alert.
     */
//    @PostMapping("/deduct")
//    public ResponseEntity<String> deductStock(
//            @RequestParam Long cbbId,
//            @RequestParam BloodGroup bloodGroup,
//            @RequestParam int quantity,
//            @RequestParam(required = false) String remarks
//    ) {
//
//        Organization cbb = organizationRepository.findById(cbbId)
//                .orElseThrow(() ->
//                        new IllegalArgumentException("CBB not found with id: " + cbbId)
//                );
//
//        inventoryService.deductStock(cbb, bloodGroup, quantity, remarks);
//
//        return ResponseEntity.ok("Blood stock deducted successfully");
//    }
}
