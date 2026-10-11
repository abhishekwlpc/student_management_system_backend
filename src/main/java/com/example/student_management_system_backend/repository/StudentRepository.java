package com.example.student_management_system_backend.repository;

import com.example.student_management_system_backend.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {

    boolean existsByRegistrationNumber(String registrationNumber);
}
