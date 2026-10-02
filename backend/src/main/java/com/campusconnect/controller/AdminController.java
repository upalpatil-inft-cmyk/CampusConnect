package com.campusconnect.controller;

import com.campusconnect.dto.*;
import com.campusconnect.entity.*;
import com.campusconnect.repository.*;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final CompanyRepository companies; private final PlacementDriveRepository drives; private final ApplicationRepository applications;
    public AdminController(CompanyRepository companies,PlacementDriveRepository drives,ApplicationRepository applications){
        this.companies=companies;this.drives=drives;this.applications=applications;
    }
    @PostMapping("/companies") public Company company(@Valid @RequestBody CompanyRequest r){return companies.save(new Company(r.name(),r.website(),r.description()));}
    @GetMapping("/companies") public Object companies(){return companies.findAll();}
    @PostMapping("/drives") public PlacementDrive drive(@Valid @RequestBody DriveRequest r){
        var c=companies.findById(r.companyId()).orElseThrow();
        return drives.save(new PlacementDrive(c,r.jobRole(),r.packageLpa(),r.minimumCgpa(),r.deadline(),r.eligibleBranches()));
    }
    @GetMapping("/drives") public Object drives(){return drives.findAll();}
    @GetMapping("/applications") public Object applications(){return applications.findAll();}
}
