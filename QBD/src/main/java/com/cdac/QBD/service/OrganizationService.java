package com.cdac.QBD.service;

import com.cdac.QBD.dto.OrganizationDto;
import com.cdac.QBD.entity.Organization;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * OrganizationService
 *
 * This service acts as a CENTRAL ACCESS POINT
 * for fetching and validating Organization entities.
 *
 * WHY THIS EXISTS:
 * - Almost every module depends on Organization
 * - We want ONE place to:
 *   - Fetch organization safely
 *   - Validate existence
 *   - Add future business rules
 *
 * Controllers and other services should NOT
 * directly fetch Organization from repository everywhere.
 */
public interface OrganizationService {

    /**
     * Fetch organization by ID.
     *
     * RULE:
     * - If organization does not exist, an exception MUST be thrown.
     * - No method should ever receive a null Organization.
     *
     * @param id Organization ID
     * @return Valid Organization entity
     */
    Organization getOrganizationById(Long id);

    List<OrganizationDto> getAllNodesByOrganisationId(Long organisationId);

     List<OrganizationDto> getAllCbb();
}

