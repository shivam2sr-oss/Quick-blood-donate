package com.cdac.QBD.dataInitializer;

import com.cdac.QBD.entity.BloodInventory;
import com.cdac.QBD.entity.Organization;
import com.cdac.QBD.repository.BloodInventoryRepository;
import com.cdac.QBD.repository.OrganizationRepository;
import com.cdac.QBD.utils.constant.BloodGroup;
import com.cdac.QBD.utils.constant.OrganizationType;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Order(2) // Ensure this runs after CbbInitializer
public class InventoryInitializer implements CommandLineRunner {

    private final OrganizationRepository organizationRepository;
    private final BloodInventoryRepository bloodInventoryRepository;

    public InventoryInitializer(OrganizationRepository organizationRepository, BloodInventoryRepository bloodInventoryRepository) {
        this.organizationRepository = organizationRepository;
        this.bloodInventoryRepository = bloodInventoryRepository;
    }

    @Override
    public void run(String... args) {
        List<Organization> cbbs = organizationRepository.findAll().stream()
                .filter(org -> org.getType() == OrganizationType.CBB)
                .toList();

        for (Organization cbb : cbbs) {
            initializeInventoryForCBB(cbb);
        }
    }

    private void initializeInventoryForCBB(Organization cbb) {
        List<BloodInventory> existingInventory = bloodInventoryRepository.findByOrganization(cbb);

        for (BloodGroup group : BloodGroup.values()) {
            boolean exists = existingInventory.stream()
                    .anyMatch(inv -> inv.getBloodGroup() == group);

            if (!exists) {
                BloodInventory inventory = new BloodInventory();
                inventory.setOrganization(cbb);
                inventory.setBloodGroup(group);
                
                // Special case for Pune CBB as requested: 30 units
                if (cbb.getCity().equalsIgnoreCase("Pune")) {
                    inventory.setQuantity(30);
                } else {
                    // Random-ish initial data for others
                    inventory.setQuantity(10 + (int) (Math.random() * 20));
                }
                
                inventory.setLastUpdated(LocalDateTime.now());
                bloodInventoryRepository.save(inventory);
                System.out.println("✅ INVENTORY INITIALIZED: Added " + inventory.getQuantity() + " units of " + group + " for " + cbb.getName());
            } else if (cbb.getCity().equalsIgnoreCase("Pune")) {
                // If it exists but it's Pune, ensure it has 30 units as requested
                BloodInventory inventory = existingInventory.stream()
                        .filter(inv -> inv.getBloodGroup() == group)
                        .findFirst()
                        .get();
                if (inventory.getQuantity() != 30) {
                    inventory.setQuantity(30);
                    inventory.setLastUpdated(LocalDateTime.now());
                    bloodInventoryRepository.save(inventory);
                    System.out.println("✅ INVENTORY UPDATED: Set 30 units of " + group + " for " + cbb.getName());
                }
            }
        }
    }
}
