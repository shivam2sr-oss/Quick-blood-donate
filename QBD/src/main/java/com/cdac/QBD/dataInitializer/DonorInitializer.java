package com.cdac.QBD.dataInitializer;

import com.cdac.QBD.entity.User;
import com.cdac.QBD.repository.UserRepository;
import com.cdac.QBD.utils.constant.BloodGroup;
import com.cdac.QBD.utils.constant.UserRole;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Order(3)
public class DonorInitializer implements CommandLineRunner {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public DonorInitializer(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        initializeSampleDonors();
    }

    private void initializeSampleDonors() {
        createDonor("itzzadi45@gmail.com", "adinath yelgatte", "1234-5678-9012",
                BloodGroup.O_POS, LocalDate.of(1995, 5, 15), "Male",
                70.0, "Shivajinagar, Pune", "9876543220", "Pune", "Pune");

        createDonor("anshulchouhan1664@gmail.com", "anshul chouhan", "2345-6789-0123",
                BloodGroup.A_POS, LocalDate.of(1998, 8, 22), "Female",
                55.0, "Kothrud, Pune", "9876543221", "Pune", "Pune");

        createDonor("patilshriyash3742@gmail.com", "Amit Kumar", "3456-7890-1234",
                BloodGroup.B_POS, LocalDate.of(1992, 3, 10), "Male",
                75.0, "Bandra, Mumbai", "9876543222", "Mumbai", "Mumbai");

        createDonor("vedantp.savalajkar@gmail.com", "vedant s", "4567-8901-2345",
                BloodGroup.AB_POS, LocalDate.of(1996, 11, 5), "Male",
                60.0, "Andheri, Mumbai", "9876543223", "Mumbai", "Mumbai");

        createDonor("adityarajapr123@gmail.com", "aditya raj", "5678-9012-3456",
                BloodGroup.O_NEG, LocalDate.of(1990, 7, 18), "Male",
                80.0, "Camp, Pune", "9876543224", "Pune", "Pune");
    }

    private void createDonor(String email, String fullName, String aadharNumber,
            BloodGroup bloodGroup, LocalDate dob, String gender,
            Double weight, String address, String contactNumber,
            String district, String city) {

        // Check if donor already exists
        boolean exists = userRepository.findAll().stream()
                .anyMatch(user -> user.getEmail().equalsIgnoreCase(email));

        if (!exists) {
            User donor = new User();
            donor.setEmail(email);
            donor.setPassword(passwordEncoder.encode("donor@123")); // 🔒 Hash password
            donor.setRole(UserRole.DONOR);
            donor.setFullName(fullName);
            donor.setAadharNumber(aadharNumber);
            donor.setBloodGroup(bloodGroup);
            donor.setDob(dob);
            donor.setGender(gender);
            donor.setWeight(weight);
            donor.setAddress(address);
            donor.setContactNumber(contactNumber);
            donor.setDistrict(district);
            donor.setCity(city);
            donor.setOrganization(null); // Donors are not linked to organizations

            userRepository.save(donor);
            System.out.println("✅ DONOR INITIALIZED: " + fullName + " (" + email + ") created successfully.");
        } else {
            System.out.println("ℹ️ DONOR CHECK: " + email + " already exists.");
        }
    }
}