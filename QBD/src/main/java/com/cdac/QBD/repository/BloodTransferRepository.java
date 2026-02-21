package com.cdac.QBD.repository;

import com.cdac.QBD.entity.BloodTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BloodTransferRepository extends JpaRepository<BloodTransfer,Long> {
}
