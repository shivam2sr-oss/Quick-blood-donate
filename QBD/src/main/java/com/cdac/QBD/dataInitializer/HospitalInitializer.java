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

@Component
@Order(4)
public class HospitalInitializer implements CommandLineRunner {

    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public HospitalInitializer(OrganizationRepository organizationRepository,
            PasswordEncoder passwordEncoder,
            UserRepository userRepository) {
        this.organizationRepository = organizationRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        initializePuneHospital();
        initializeMumbaiHospital();
    }

    private void initializePuneHospital() {
        String hospitalName = "Pune City Hospital";
        String city = "Pune";
        OrganizationType type = OrganizationType.HOSPITAL;

        // Check if hospital already exists
        boolean exists = organizationRepository.findAll().stream()
                .anyMatch(org -> org.getName().equalsIgnoreCase(hospitalName)
                        && org.getCity().equalsIgnoreCase(city)
                        && org.getType() == type);

        if (!exists) {
            Organization hospital = new Organization();
            hospital.setName(hospitalName);
            hospital.setType(OrganizationType.HOSPITAL);
            hospital.setCity("Pune");
            hospital.setState("Maharashtra");
            hospital.setDistrict("Pune");
            hospital.setAddress("Camp Area, Near Railway Station, Pune");
            hospital.setContactNumber("020-98765432");

            Organization savedHospital = organizationRepository.save(hospital);

            User user = new User();
            user.setEmail("punehospital@gmail.com");
            user.setPassword(passwordEncoder.encode("punehospital@123")); // 🔒 Hash password
            user.setRole(UserRole.HOSPITAL_STAFF);
            user.setOrganization(savedHospital);
            user.setFullName("Pune Hospital Admin");
            user.setContactNumber("9876543211");
            user.setDistrict("Pune");
            user.setCity("Pune");
            userRepository.save(user);

            System.out.println("✅ HOSPITAL INITIALIZED: Pune City Hospital created successfully.");
        } else {
            System.out.println("ℹ️ HOSPITAL CHECK: Pune City Hospital already exists.");
        }
    }

    private void initializeMumbaiHospital() {
        String hospitalName = "Mumbai General Hospital";
        String city = "Mumbai";
        OrganizationType type = OrganizationType.HOSPITAL;

        // Check if hospital already exists
        boolean exists = organizationRepository.findAll().stream()
                .anyMatch(org -> org.getName().equalsIgnoreCase(hospitalName)
                        && org.getCity().equalsIgnoreCase(city)
                        && org.getType() == type);

        if (!exists) {
            Organization hospital = new Organization();
            hospital.setName(hospitalName);
            hospital.setType(OrganizationType.HOSPITAL);
            hospital.setCity("Mumbai");
            hospital.setState("Maharashtra");
            hospital.setDistrict("Mumbai");
            hospital.setAddress("Andheri West, Mumbai");
            hospital.setContactNumber("022-87654321");

            Organization savedHospital = organizationRepository.save(hospital);

            User user = new User();
            user.setEmail("mumbaihospital@gmail.com");
            user.setPassword(passwordEncoder.encode("mumbaihospital@123")); // 🔒 Hash password
            user.setRole(UserRole.HOSPITAL_STAFF);
            user.setOrganization(savedHospital);
            user.setFullName("Mumbai Hospital Admin");
            user.setContactNumber("9876543212");
            user.setDistrict("Mumbai");
            user.setCity("Mumbai");
            userRepository.save(user);

            System.out.println("✅ HOSPITAL INITIALIZED: Mumbai General Hospital created successfully.");
        } else {
            System.out.println("ℹ️ HOSPITAL CHECK: Mumbai General Hospital already exists.");
        }
    }
}