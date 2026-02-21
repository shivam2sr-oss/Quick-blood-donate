package com.cdac.QBD.repository;

import com.cdac.QBD.entity.MedicalHistory;
import com.cdac.QBD.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicalHistoryRepository extends JpaRepository<MedicalHistory,Long> {
}
