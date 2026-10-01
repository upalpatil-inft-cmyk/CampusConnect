package com.campusconnect.repository;
import com.campusconnect.entity.*;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
public interface MarkRepository extends JpaRepository<Mark,Long>{ List<Mark> findByStudent(Student student); }