package com.cdac.QBD.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "medical_history")
public class MedicalHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) // Foreign Key
    private User user;

    @Column(nullable = false)
    private String conditionName;

    // specific constructor for easy creation
    public MedicalHistory(User user, String conditionName) {
        this.user = user;
        this.conditionName = conditionName;
    }
}