package com.campusconnect.repository;
import com.campusconnect.entity.Faculty;
import com.campusconnect.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
public interface FacultyRepository extends JpaRepository<Faculty,Long>{ Optional<Faculty> findByUser(User user); }