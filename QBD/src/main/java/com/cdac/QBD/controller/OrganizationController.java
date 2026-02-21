package com.cdac.QBD.controller;

import com.cdac.QBD.entity.Organization;
import com.cdac.QBD.repository.OrganizationRepository;
import com.cdac.QBD.service.OrganizationService;
import com.cdac.QBD.utils.constant.OrganizationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizations")
public class OrganizationController {

    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private OrganizationService organizationService;
    /**
     * Get Collection Nodes.
     * Usage 1: /api/organizations/nodes             (Returns ALL nodes)
     * Usage 2: /api/organizations/nodes?city=Pune   (Returns nodes in Pune only)
     */
    @GetMapping("/nodes")
    public ResponseEntity<List<Organization>> getAllNodes(
            @RequestParam(required = false) String city) {

        // 1. If 'city' parameter is provided, filter by city (Case Insensitive)
        if (city != null && !city.trim().isEmpty()) {
            List<Organization> filteredNodes = organizationRepository
                    .findByTypeAndCityIgnoreCase(OrganizationType.NODE, city);

            // Optional: If no nodes found in city, you could return an empty list
            // or fallback to all nodes. For now, we return the filtered list (even if empty).
            return ResponseEntity.ok(filteredNodes);
        }

        // 2. Default: Return ALL nodes if no city is specified
        return ResponseEntity.ok(organizationRepository.findByType(OrganizationType.NODE));
    }

    @GetMapping("/nodes/{organisationId}")
    public ResponseEntity<?> getAllNodesByOrganisationId(@PathVariable Long organisationId) {
        return ResponseEntity.ok(organizationService.getAllNodesByOrganisationId( organisationId));
    }

}