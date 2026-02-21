package com.cdac.QBD.repository;

import com.cdac.QBD.entity.DonationCamp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DonationCampRepository extends JpaRepository<DonationCamp, Long> {

    // Efficiently fetch camps for a specific node
    List<DonationCamp> findByOrganizationId(Long organizationId);

    // Efficiently fetch future camps
    List<DonationCamp> findByCampDateGreaterThanEqual(LocalDate date);
}