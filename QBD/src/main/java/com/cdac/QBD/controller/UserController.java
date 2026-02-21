package com.cdac.QBD.controller;

import com.cdac.QBD.entity.User;
import com.cdac.QBD.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    /**
     * Endpoint: GET /api/users/current
     * Purpose: Returns the full profile of the currently logged-in user.
     * Used by: NodeDashboard (to find Organization ID) and DonorDashboard (to find Profile).
     */
    @GetMapping("/current")
    public ResponseEntity<User> getCurrentUser() {
        // 1. Get the email from the Spring Security Context (set by the JWT Filter)
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        // 2. Fetch the user from the database
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 3. Return the user object
        return ResponseEntity.ok(user);
    }
}