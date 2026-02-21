package com.cdac.QBD.dto;

import com.cdac.QBD.utils.constant.BloodGroup;
import com.cdac.QBD.utils.constant.RequestStatus;
import com.cdac.QBD.utils.constant.UrgencyLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for Hospital Request Response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HospitalRequestResponseDto {
    
    private Long id;
    
    private Long hospitalId;
    
    private String hospitalName;
    
    private Long cbbId;
    
    private String cbbName;
    
    private BloodGroup bloodGroup;
    
    private int unitsNeeded;
    
    private UrgencyLevel urgency;
    
    private RequestStatus status;
    
    private LocalDateTime requestDate;
    
    /**
     * Optional: Flag to indicate if 12-hour SLA was breached
     */
    private Boolean slaBreached;
    
    /**
     * Hours elapsed since request creation
     */
    private Long hoursElapsed;
}
