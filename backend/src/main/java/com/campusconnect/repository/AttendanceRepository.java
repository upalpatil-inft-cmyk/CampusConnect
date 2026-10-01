package com.campusconnect.repository;
import com.campusconnect.entity.*;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AttendanceRepository extends JpaRepository<Attendance,Long>{ List<Attendance> findByStudent(Student student); List<Attendance> findBySubject(Subject subject); }