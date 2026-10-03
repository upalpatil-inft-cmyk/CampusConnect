package com.campusconnect.controller;

import com.campusconnect.entity.Application;
import com.campusconnect.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @GetMapping("/drives")
    @PreAuthorize("hasRole('STUDENT')")
    public Object drives(){
        return drives.findAll().stream().map(d -> java.util.Map.of(
            "id",d.getId(),"company",d.getCompany().getName(),"website",d.getCompany().getWebsite()==null?"":d.getCompany().getWebsite(),
            "description",d.getCompany().getDescription()==null?"":d.getCompany().getDescription(),
            "jobRole",d.getJobRole(),"packageLpa",d.getPackageLpa(),"minimumCgpa",d.getMinimumCgpa(),
            "deadline",d.getDeadline(),"eligibleBranches",d.getEligibleBranches()==null?"":d.getEligibleBranches()
        )).toList();
    }

    @PostMapping("/drives/{id}/apply")
    @PreAuthorize("hasRole('STUDENT')")
    public Object apply(Authentication a,@PathVariable Long id){
        var student=students.findByUser(users.findByEmail(a.getName()).orElseThrow()).orElseThrow();
        var drive=drives.findById(id).orElseThrow();
        if (drive.getDeadline().isBefore(java.time.LocalDate.now())) throw new IllegalArgumentException("This placement drive is closed.");
        if(student.getCgpa()<drive.getMinimumCgpa()) throw new IllegalArgumentException("Student does not meet CGPA eligibility");
        var branches=drive.getEligibleBranches();
        if (branches != null && !branches.isBlank()) {
            boolean branchEligible=java.util.Arrays.stream(branches.split(",")).map(String::trim)
                    .anyMatch(x -> x.equalsIgnoreCase(student.getDepartment().getCode()) || x.equalsIgnoreCase(student.getDepartment().getName()));
            if (!branchEligible) throw new IllegalArgumentException("Student is not eligible for this branch.");
        }
        if(applications.existsByPlacementDriveAndStudent(drive,student)) throw new IllegalArgumentException("Already applied");
        var saved=applications.save(new Application(drive,student));
        return java.util.Map.of("id",saved.getId(),"company",drive.getCompany().getName(),"role",drive.getJobRole(),"status",saved.getStatus().name());
    }
}
