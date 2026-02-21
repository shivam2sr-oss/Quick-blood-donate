package com.cdac.QBD.serviceImplementation;

import com.cdac.QBD.dto.AuthResponseDTO;
import com.cdac.QBD.dto.LoginRequestDTO;
import com.cdac.QBD.dto.SignupRequestDTO;
import com.cdac.QBD.entity.MedicalHistory;
import com.cdac.QBD.entity.Organization;
import com.cdac.QBD.entity.User;
import com.cdac.QBD.repository.MedicalHistoryRepository;
import com.cdac.QBD.repository.OrganizationRepository;
import com.cdac.QBD.repository.UserRepository;
import com.cdac.QBD.service.UserService;
import com.cdac.QBD.utils.constant.BloodGroup;
import com.cdac.QBD.utils.constant.OrganizationType;
import com.cdac.QBD.utils.constant.UserRole;
import com.cdac.QBD.utils.security.JWTHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserDetailService userDetailService;
    @Autowired
    private JWTHelper jwtHelper;
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private MedicalHistoryRepository medicalHistoryRepository;

    // --- REGISTRATION LOGIC START ---

    @Override
    public boolean createUser(SignupRequestDTO request) {
        // 1. Validation
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use.");
        }

        // 2. Delegate to specific helper based on Role
        switch (request.getRole()) {
            case "DONOR":
                return registerDonor(request);
            case "NODE_STAFF":
                return registerNodeStaff(request);
            case "CBB_STAFF":
                return registerCbbStaff(request);
            case "HOSPITAL_STAFF":
                return registerHospitalStaff(request);
            case "ADMIN":
                return registerAdmin(request);
            default:
                throw new RuntimeException("Invalid Role");
        }
    }

    private User createBasicUser(SignupRequestDTO request, UserRole role) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // 🔒 Hash password here
        user.setRole(role);
        user.setFullName(request.getFullName());
        user.setContactNumber(request.getContactNumber());
        user.setAddress(request.getAddress());
        // Set city and district - use city field if provided, otherwise fallback to
        // district
        user.setCity(request.getCity() != null ? request.getCity() : request.getDistrict());
        user.setDistrict(request.getDistrict());
        return userRepository.save(user);
    }

    private boolean registerDonor(SignupRequestDTO request) {
        // A. Create the User Account
        User savedUser = createBasicUser(request, UserRole.DONOR);

        // B. Add Donor Specific Details
        if (request.getBloodGroup() != null) {
            savedUser.setBloodGroup(BloodGroup.valueOf(request.getBloodGroup()));
        }
        savedUser.setWeight(request.getWeight());
        savedUser.setDob(request.getDob());
        savedUser.setGender(request.getGender());

        // C. Save Medical History (If provided)
        if (request.getMedicalHistory() != null) {
            for (String condition : request.getMedicalHistory()) {
                MedicalHistory history = new MedicalHistory();
                history.setUser(savedUser); // Link to user
                history.setConditionName(condition);
                medicalHistoryRepository.save(history);
            }
        }

        userRepository.save(savedUser);
        return true;
    }

    private boolean registerNodeStaff(SignupRequestDTO request) {
        User savedUser = createBasicUser(request, UserRole.NODE_STAFF);

        // Create the Node Organization
        Organization org = new Organization();
        org.setName(request.getFullName() + " Node");
        org.setType(OrganizationType.NODE);
        org.setCity(request.getAddress());
        org.setDistrict(request.getDistrict());
        org.setContactNumber(request.getContactNumber());

        org = organizationRepository.save(org);

        // Link User -> Org
        savedUser.setOrganization(org);
        userRepository.save(savedUser);
        return true;
    }

    // ... (Keep CBB_STAFF, HOSPITAL_STAFF, and ADMIN methods as you had them) ...
    // They follow the exact same pattern as registerNodeStaff

    private boolean registerCbbStaff(SignupRequestDTO request) {
        User savedUser = createBasicUser(request, UserRole.CBB_STAFF);
        Organization org = new Organization();
        org.setName(request.getFullName() + " CBB");
        org.setType(OrganizationType.CBB);
        org.setCity(request.getAddress());
        org.setDistrict(request.getDistrict());
        org.setContactNumber(request.getContactNumber());
        org = organizationRepository.save(org);
        savedUser.setOrganization(org);
        userRepository.save(savedUser);
        return true;
    }

    private boolean registerHospitalStaff(SignupRequestDTO request) {
        User savedUser = createBasicUser(request, UserRole.HOSPITAL_STAFF);
        Organization parent = organizationRepository.findById(Long.valueOf(request.getParentOrganizationId())).get();
        Organization org = new Organization();
        org.setName(request.getFullName() + " Hospital");
        org.setType(OrganizationType.HOSPITAL);
        org.setCity(request.getAddress());
        org.setDistrict(request.getDistrict());
        org.setContactNumber(request.getContactNumber());
        org.setParentOrganization(parent);
        org = organizationRepository.save(org);
        savedUser.setOrganization(org);
        userRepository.save(savedUser);
        return true;
    }

    private boolean registerAdmin(SignupRequestDTO request) {
        createBasicUser(request, UserRole.ADMIN);
        return true;
    }

    // --- AUTHENTICATION LOGIC ---

    @Override
    public AuthResponseDTO login(LoginRequestDTO request) {
        try {
            UserDetails userDetails = userDetailService.loadUserByUsername(request.getUsername());

            if (!passwordEncoder.matches(request.getPassword(), userDetails.getPassword())) {
                throw new BadCredentialsException("Invalid credentials");
            }

            String jwtToken = jwtHelper.generateToken(userDetails);
            return AuthResponseDTO.builder().jwtToken(jwtToken).build();

        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
    }

    @Override
    public User getCurrentUser() {
        if (SecurityContextHolder.getContext().getAuthentication() == null)
            return null;
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElse(null);
    }
}