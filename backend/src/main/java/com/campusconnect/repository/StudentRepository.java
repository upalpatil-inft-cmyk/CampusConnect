package com.campusconnect.repository;
import com.campusconnect.entity.Student;
import com.campusconnect.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
public interface StudentRepository extends JpaRepository<Student,Long>{ Optional<Student> findByUser(User user); }