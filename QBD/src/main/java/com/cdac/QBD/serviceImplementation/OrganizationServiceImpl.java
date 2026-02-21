package com.cdac.QBD.serviceImplementation;

import com.cdac.QBD.dto.OrganizationDto;
import com.cdac.QBD.entity.Organization;
import com.cdac.QBD.repository.OrganizationRepository;
import com.cdac.QBD.service.OrganizationService;
import com.cdac.QBD.utils.constant.OrganizationType;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * OrganizationServiceImpl
 *
 * This class provides the ACTUAL implementation
 * of OrganizationService.
 *
 * CURRENT RESPONSIBILITIES:
 * 1. Fetch organization from database
 * 2. Validate existence
 *
 * FUTURE RESPONSIBILITIES (very important):
 * - Validate organization type (CBB / NODE / HOSPITAL)
 * - Validate parent-child relationships
 * - Location-based resolution
 * - JWT-based organization scoping
 *
 * By centralizing logic here, we keep:
 * - Controllers thin
 * - Business rules consistent
 */
@Service
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepository organizationRepository;

    /**
     * Constructor injection.
     *
     * Spring will automatically inject OrganizationRepository.
     */
    public OrganizationServiceImpl(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    /**
     * Fetch Organization by ID.
     *
     * IMPLEMENTATION DETAILS:
     * - Calls repository
     * - If organization does NOT exist:
     *     -> Throws IllegalArgumentException
     * - This exception is later converted to:
     *     -> HTTP 400 Bad Request
     *       (via GlobalExceptionHandler)
     *
     * WHY NOT RETURN OPTIONAL?
     * - Forces callers to handle existence
     * - Prevents null-related bugs
     *
     * @param id Organization ID
     * @return Valid Organization entity
     */
    @Override
    public Organization getOrganizationById(Long id) {

        return organizationRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Organization not found with id: " + id
                        )
                );
    }

    @Override
    public List<OrganizationDto> getAllNodesByOrganisationId(Long organisationId) {
        List<Organization> organizations = organizationRepository.findByParentOrganizationId(organisationId);
        System.out.println(organizations);
        return organizations.stream().map(this::toDto).toList();
    }

    @Override
    public List<OrganizationDto> getAllCbb() {
        List<Organization> organizations = organizationRepository.findAllByType(OrganizationType.CBB);
        System.out.println(organizations);
        return organizations.stream().map(this::toDto).toList();
    }

    private OrganizationDto toDto(Organization organization) {
        OrganizationDto dto = new OrganizationDto();
        dto.setId(organization.getId());
        dto.setName(organization.getName());
        dto.setType(organization.getType().name());
        dto.setAddress(organization.getAddress());
        dto.setState(organization.getState());
        dto.setCity(organization.getCity());
        dto.setDistrict(organization.getDistrict());
        if(organization.getParentOrganization()!=null){

            dto.setParentOrganizationId(organization.getParentOrganization().
                    getId());
        }
        return dto;
    }
}
