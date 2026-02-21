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
@Order(5)
public class BranchInitializer implements CommandLineRunner {

    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public BranchInitializer(OrganizationRepository organizationRepository,
            PasswordEncoder passwordEncoder,
            UserRepository userRepository) {
        this.organizationRepository = organizationRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        initializePuneNode();
        initializeMumbaiNode();
    }

    private void initializePuneNode() {
        String nodeName = "Pune Kothrud Blood Collection Center";
        String city = "Pune";
        OrganizationType type = OrganizationType.NODE;

        // Check if node already exists
        boolean exists = organizationRepository.findAll().stream()
                .anyMatch(org -> org.getName().equalsIgnoreCase(nodeName)
                        && org.getCity().equalsIgnoreCase(city)
                        && org.getType() == type);

        if (!exists) {
            // Find parent CBB (Pune CBB)
            Optional<Organization> parentCBB = organizationRepository.findAll().stream()
                    .filter(org -> org.getCity().equalsIgnoreCase("Pune")
                            && org.getType() == OrganizationType.CBB)
                    .findFirst();

            Organization node = new Organization();
            node.setName(nodeName);
            node.setType(OrganizationType.NODE);
            node.setCity("Pune");
            node.setState("Maharashtra");
            node.setDistrict("Pune");
            node.setAddress("Kothrud, Near Karve Road, Pune");
            node.setContactNumber("020-55555555");

            // Link to parent CBB if exists
            parentCBB.ifPresent(node::setParentOrganization);

            Organization savedNode = organizationRepository.save(node);

            User user = new User();
            user.setEmail("punebranch@gmail.com");
            user.setPassword(passwordEncoder.encode("punebranch@123")); // 🔒 Hash password
            user.setRole(UserRole.NODE_STAFF);
            user.setOrganization(savedNode);
            user.setFullName("Pune Branch Admin");
            user.setContactNumber("9876543213");
            user.setDistrict("Pune");
            user.setCity("Pune");
            userRepository.save(user);

            System.out.println("✅ BRANCH INITIALIZED: Pune Kothrud Blood Collection Center created successfully.");
        } else {
            System.out.println("ℹ️ BRANCH CHECK: Pune Kothrud Blood Collection Center already exists.");
        }
    }

    private void initializeMumbaiNode() {
        String nodeName = "Mumbai Bandra Blood Collection Center";
        String city = "Mumbai";
        OrganizationType type = OrganizationType.NODE;

        // Check if node already exists
        boolean exists = organizationRepository.findAll().stream()
                .anyMatch(org -> org.getName().equalsIgnoreCase(nodeName)
                        && org.getCity().equalsIgnoreCase(city)
                        && org.getType() == type);

        if (!exists) {
            // Find parent CBB (could be Pune CBB or create Mumbai CBB first)
            Optional<Organization> parentCBB = organizationRepository.findAll().stream()
                    .filter(org -> org.getType() == OrganizationType.CBB)
                    .findFirst();

            Organization node = new Organization();
            node.setName(nodeName);
            node.setType(OrganizationType.NODE);
            node.setCity("Mumbai");
            node.setState("Maharashtra");
            node.setDistrict("Mumbai");
            node.setAddress("Bandra West, Near Linking Road, Mumbai");
            node.setContactNumber("022-66666666");

            // Link to parent CBB if exists
            parentCBB.ifPresent(node::setParentOrganization);

            Organization savedNode = organizationRepository.save(node);

            User user = new User();
            user.setEmail("mumbaibranch@gmail.com");
            user.setPassword(passwordEncoder.encode("mumbaibranch@123")); // 🔒 Hash password
            user.setRole(UserRole.NODE_STAFF);
            user.setOrganization(savedNode);
            user.setFullName("Mumbai Branch Admin");
            user.setContactNumber("9876543214");
            user.setDistrict("Mumbai");
            user.setCity("Mumbai");
            userRepository.save(user);

            System.out.println("✅ BRANCH INITIALIZED: Mumbai Bandra Blood Collection Center created successfully.");
        } else {
            System.out.println("ℹ️ BRANCH CHECK: Mumbai Bandra Blood Collection Center already exists.");
        }
    }
}