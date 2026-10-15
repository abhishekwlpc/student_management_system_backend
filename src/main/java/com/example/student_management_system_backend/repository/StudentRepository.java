package com.example.student_management_system_backend.repository;

import com.example.student_management_system_backend.entity.Student;
import com.example.student_management_system_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {

    boolean existsByRegistrationNumber(String registrationNumber);

    Student findByUser(User user);

    Optional<Student> findByUser_Username(String username);
}
