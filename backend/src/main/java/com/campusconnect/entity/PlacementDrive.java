package com.campusconnect.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class PlacementDrive {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional=false) private Company company;
    @Column(nullable=false) private String jobRole;
    @Column(nullable=false) private double packageLpa;
    @Column(nullable=false) private double minimumCgpa;
    @Column(nullable=false) private LocalDate deadline;
    @Column(length=1000) private String eligibleBranches;

    public PlacementDrive() {}
    public PlacementDrive(Company company,String jobRole,double packageLpa,double minimumCgpa,LocalDate deadline,String eligibleBranches){
        this.company=company;this.jobRole=jobRole;this.packageLpa=packageLpa;this.minimumCgpa=minimumCgpa;this.deadline=deadline;this.eligibleBranches=eligibleBranches;
    }
    public Long getId(){return id;} public Company getCompany(){return company;} public String getJobRole(){return jobRole;}
    public double getPackageLpa(){return packageLpa;} public double getMinimumCgpa(){return minimumCgpa;}
    public LocalDate getDeadline(){return deadline;} public String getEligibleBranches(){return eligibleBranches;}
}
