package com.campusconnect.controller;

import com.campusconnect.entity.ApplicationStatus;
import com.campusconnect.repository.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/placement-upgrades")
public class PlacementUpgradeController {
    private final PlacementDriveRepository drives;
    private final ApplicationRepository applications;
    private final StudentRepository students;
    private final UserRepository users;

    public PlacementUpgradeController(PlacementDriveRepository drives, ApplicationRepository applications,
                                      StudentRepository students, UserRepository users) {
        this.drives = drives;
        this.applications = applications;
        this.students = students;
        this.users = users;
    }

    @GetMapping("/student")
    public Object studentPlacements(Authentication a) {
        var student = students.findByUser(users.findByEmail(a.getName()).orElseThrow()).orElseThrow();
        var myApps = applications.findByStudent(student);
        var appByDrive = new HashMap<Long, com.campusconnect.entity.Application>();
        myApps.forEach(x -> appByDrive.put(x.getPlacementDrive().getId(), x));

        return drives.findAll().stream().map(d -> {
            var app = appByDrive.get(d.getId());
            boolean cgpaEligible = student.getCgpa() >= d.getMinimumCgpa();
            String departmentCode = student.getDepartment().getCode();
            String departmentName = student.getDepartment().getName();
            boolean branchEligible = d.getEligibleBranches() == null || d.getEligibleBranches().isBlank()
                    || Arrays.stream(d.getEligibleBranches().split(","))
                        .map(String::trim)
                        .anyMatch(x -> x.equalsIgnoreCase(departmentCode) || x.equalsIgnoreCase(departmentName));
            return Map.of(
                "id", d.getId(),
                "company", d.getCompany().getName(),
                "website", d.getCompany().getWebsite() == null ? "" : d.getCompany().getWebsite(),
                "description", d.getCompany().getDescription() == null ? "" : d.getCompany().getDescription(),
                "jobRole", d.getJobRole(),
                "packageLpa", d.getPackageLpa(),
                "minimumCgpa", d.getMinimumCgpa(),
                "deadline", d.getDeadline(),
                "eligibleBranches", d.getEligibleBranches() == null ? "" : d.getEligibleBranches(),
                "cgpaEligible", cgpaEligible,
                "branchEligible", branchEligible,
                "eligible", cgpaEligible && branchEligible,
                "applicationStatus", app == null ? "" : app.getStatus().name(),
                "appliedAt", app == null ? "" : app.getAppliedAt()
            );
        }).toList();
    }

    @GetMapping("/history")
    public Object placementHistory(Authentication a) {
        var student = students.findByUser(users.findByEmail(a.getName()).orElseThrow()).orElseThrow();
        return applications.findByStudent(student).stream()
                .filter(x -> x.getStatus() == ApplicationStatus.SELECTED)
                .map(x -> Map.of(
                    "company", x.getPlacementDrive().getCompany().getName(),
                    "role", x.getPlacementDrive().getJobRole(),
                    "packageLpa", x.getPlacementDrive().getPackageLpa(),
                    "selectedAt", x.getAppliedAt()
                )).toList();
    }

    @GetMapping("/stats")
    public Object placementStats(Authentication a) {
        var student = students.findByUser(users.findByEmail(a.getName()).orElseThrow()).orElseThrow();
        var apps = applications.findByStudent(student);
        long selected = apps.stream().filter(x -> x.getStatus() == ApplicationStatus.SELECTED).count();
        long active = apps.stream().filter(x -> x.getStatus() != ApplicationStatus.REJECTED && x.getStatus() != ApplicationStatus.SELECTED).count();
        return Map.of(
                "applications", apps.size(),
                "activeApplications", active,
                "selected", selected,
                "openDrives", drives.findAll().stream().filter(x -> !x.getDeadline().isBefore(LocalDate.now())).count()
        );
    }

    @PatchMapping("/admin/applications/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Object updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        var app = applications.findById(id).orElseThrow();
        var raw = body.get("status");
        if (raw == null || raw.isBlank()) throw new IllegalArgumentException("Status is required");
        var status = ApplicationStatus.valueOf(raw.toUpperCase(Locale.ROOT));
        app.setStatus(status);
        var saved = applications.save(app);
        return Map.of(
            "id", saved.getId(),
            "status", saved.getStatus().name(),
            "company", saved.getPlacementDrive().getCompany().getName(),
            "student", saved.getStudent().getUser().getFullName()
        );
    }
}
