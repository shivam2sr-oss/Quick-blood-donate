package com.cdac.QBD.dto;

import com.cdac.QBD.utils.constant.BloodGroup;
import com.cdac.QBD.utils.constant.UrgencyLevel;
import lombok.Data;

/**
 * DTO for Creating Hospital Blood Request
 */
@Data
public class HospitalRequestCreateDto {

    private Long hospitalId;
    private Long cbbId;
    private BloodGroup bloodGroup;
    private int unitsNeeded;
    private UrgencyLevel urgency;
}
