package com.cdac.QBD.entity;

import com.cdac.QBD.utils.constant.OrganizationType;
import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
@Table(name = "organizations")
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrganizationType type;


    // --- Location Data ---
    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String district;

//    @Column(nullable = false)
    private String state;

    private String address;
    private String contactNumber;

    // --- The "Mesh" Logic ---

    // If this is a NODE, this points to its CBB. If CBB, this is null.
    @ManyToOne
    @JoinColumn(name = "parent_org_id")
    private Organization parentOrganization;


}