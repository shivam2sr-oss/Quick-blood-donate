package com.cdac.QBD.repository;


import com.cdac.QBD.entity.DonationRequest;
import com.cdac.QBD.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DonationRequestRepository extends JpaRepository<DonationRequest,Long> {
    List<DonationRequest> findByDonor(User donor);

}
