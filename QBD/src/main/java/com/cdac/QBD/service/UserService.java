package com.cdac.QBD.service;

import com.cdac.QBD.dto.AuthResponseDTO;
import com.cdac.QBD.dto.LoginRequestDTO;
import com.cdac.QBD.dto.SignupRequestDTO;
import com.cdac.QBD.entity.User;

public interface UserService {
    boolean createUser(SignupRequestDTO request);

    AuthResponseDTO login(LoginRequestDTO request);

    User getCurrentUser();
}
