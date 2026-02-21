package com.cdac.QBD.repository;

import com.cdac.QBD.entity.HospitalRequest;
import com.cdac.QBD.entity.Organization;
import com.cdac.QBD.utils.constant.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HospitalRequestRepository extends JpaRepository<HospitalRequest,Long> {
    List<HospitalRequest> findByHospitalId(Long hospitalId);

    List<HospitalRequest> findByHospitalIdAndStatus(Long hospitalId, RequestStatus requestStatus);

    List<HospitalRequest> findByHospital(Organization hospital);
}
