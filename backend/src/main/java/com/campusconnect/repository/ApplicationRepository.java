package com.campusconnect.repository;
import com.campusconnect.entity.*;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ApplicationRepository extends JpaRepository<Application,Long>{ List<Application> findByStudent(Student student); List<Application> findByPlacementDrive(PlacementDrive drive); boolean existsByPlacementDriveAndStudent(PlacementDrive drive,Student student); }