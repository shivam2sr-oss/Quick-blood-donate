package com.cdac.QBD.controller;

import com.cdac.QBD.dto.LoginRequestDTO;
import com.cdac.QBD.dto.SignupRequestDTO;
import com.cdac.QBD.service.OrganizationService;
import com.cdac.QBD.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserService userService;
    @Autowired
    private OrganizationService organizationService;

    @PostMapping("/signup")
    public ResponseEntity<?> registerBorrower(@RequestBody SignupRequestDTO request) {
        System.out.println(request);
        return ResponseEntity.ok(userService.createUser(request));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        System.out.println("called");
        return ResponseEntity.ok(userService.login(request));
    }
    @GetMapping("/cbb")
    public ResponseEntity<?> getAllCbb(){
        return ResponseEntity.ok(organizationService.getAllCbb());
    }
}