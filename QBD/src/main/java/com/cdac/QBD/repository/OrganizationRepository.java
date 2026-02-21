package com.cdac.QBD.repository;

import com.cdac.QBD.entity.Organization;
import com.cdac.QBD.utils.constant.OrganizationType;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrganizationRepository extends JpaRepository<Organization,Long> {

    List<Organization> findByType(OrganizationType organizationType);



    List<Organization> findByTypeAndCityIgnoreCase(OrganizationType organizationType, String city);

    List<Organization> findByParentOrganizationId(Long organisationId);

    List<Organization> findAllByType(OrganizationType type);
}
