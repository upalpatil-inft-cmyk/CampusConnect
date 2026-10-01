package com.campusconnect.controller;

import com.campusconnect.entity.Application;
import com.campusconnect.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/placements")
public class PlacementController {
    private final PlacementDriveRepository drives; private final CompanyRepository companies;
    private final ApplicationRepository applications; private final StudentRepository students; private final UserRepository users;

    public PlacementController(PlacementDriveRepository drives,CompanyRepository companies,ApplicationRepository applications,
                               StudentRepository students,UserRepository users){
        this.drives=drives;this.companies=companies;this.applications=applications;this.students=students;this.users=users;
    }

    @GetMapping("/drives") public Object drives(){return drives.findAll();}

    @PostMapping("/drives/{id}/apply")
    public Object apply(Authentication a,@PathVariable Long id){
        var student=students.findByUser(users.findByEmail(a.getName()).orElseThrow()).orElseThrow();
        var drive=drives.findById(id).orElseThrow();
        if(student.getCgpa()<drive.getMinimumCgpa()) throw new IllegalArgumentException("Student does not meet CGPA eligibility");
        if(applications.existsByPlacementDriveAndStudent(drive,student)) throw new IllegalArgumentException("Already applied");
        return applications.save(new Application(drive,student));
    }
}
