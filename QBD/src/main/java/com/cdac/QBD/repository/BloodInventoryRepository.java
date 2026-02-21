package com.cdac.QBD.repository;

import com.cdac.QBD.entity.BloodInventory;
import com.cdac.QBD.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BloodInventoryRepository extends JpaRepository<BloodInventory,Long> {

    List<BloodInventory> findByOrganization(Organization organization);
}
