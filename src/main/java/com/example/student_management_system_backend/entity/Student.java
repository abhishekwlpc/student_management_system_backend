package com.example.student_management_system_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "students")
@Getter
@Setter
public class Student {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(name = "student_id")
    private int id;

    @Column(name = "registration_number", nullable = false, unique = true, length = 50)
    private String registrationNumber;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "birth_date", nullable = false)
    private java.time.LocalDate birthDate;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "guardian_name")
    private String guardianName;

    @Column(name = "guardian_relationship")
    private String guardianRelationship;

    @Column(name = "guardian_telephone", length = 30)
    private String guardianTelephone;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false, unique = true)
    private User user;
}
