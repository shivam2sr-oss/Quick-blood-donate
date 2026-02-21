package com.cdac.QBD.dataInitializer;

import com.cdac.QBD.entity.Organization;
import com.cdac.QBD.entity.User;
import com.cdac.QBD.repository.OrganizationRepository;
import com.cdac.QBD.repository.UserRepository;
import com.cdac.QBD.utils.constant.OrganizationType;
import com.cdac.QBD.utils.constant.UserRole;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Order(1)
public class CbbInitializer implements CommandLineRunner {

    private final OrganizationRepository organizationRepository;

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public CbbInitializer(OrganizationRepository organizationRepository, PasswordEncoder passwordEncoder,
            UserRepository userRepository) {
        this.organizationRepository = organizationRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        initializeCBB("Pune", "Pune Central Blood Bank (Headquarters)", "Sector 7, Shivajinagar, Pune",
                "puneCbb@gmail.com", "punecbb@123");
        initializeCBB("Mumbai", "Mumbai Central Blood Bank", "Fort, Mumbai", "mumbaiCbb@gmail.com", "mumbaicbb@123");
        initializeCBB("Nagpur", "Nagpur Central Blood Bank", "Civil Lines, Nagpur", "nagpurCbb@gmail.com",
                "nagpurcbb@123");
        initializeCBB("Delhi", "Delhi Central Blood Bank", "Connaught Place, Delhi", "delhiCbb@gmail.com",
                "delhicbb@123");
    }

    private void initializeCBB(String city, String name, String address, String email, String password) {
        OrganizationType type = OrganizationType.CBB;

        boolean exists = organizationRepository.findAll().stream()
                .anyMatch(org -> org.getCity().equalsIgnoreCase(city) && org.getType() == type);

        if (!exists) {
            Organization cbb = new Organization();
            cbb.setName(name);
            cbb.setType(OrganizationType.CBB);
            cbb.setCity(city);
            cbb.setState(city.equals("Delhi") ? "Delhi" : "Maharashtra");
            cbb.setDistrict(city);
            cbb.setAddress(address);
            cbb.setContactNumber("020-12345678");

            Organization saved = organizationRepository.save(cbb);
            User user = new User();
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(UserRole.CBB_STAFF);
            user.setOrganization(saved);
            user.setFullName(city + " CBB Admin");
            user.setContactNumber("9876543210");
            user.setDistrict(city);
            user.setCity(city);
            userRepository.save(user);
            System.out.println("✅ CBB INITIALIZED: " + name + " created successfully.");
        } else {
            System.out.println("ℹ️ CBB CHECK: " + name + " already exists.");
        }
    }
}