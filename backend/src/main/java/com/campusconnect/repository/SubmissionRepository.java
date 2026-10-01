package com.campusconnect.repository;
import com.campusconnect.entity.*;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
public interface SubmissionRepository extends JpaRepository<Submission,Long>{ List<Submission> findByStudent(Student student); List<Submission> findByAssignment(Assignment assignment); }