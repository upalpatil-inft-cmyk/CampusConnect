package com.campusconnect.controller;

import com.campusconnect.entity.Role;
import com.campusconnect.repository.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.*;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminOverviewController {
    private final UserRepository users;
    private final StudentRepository students;
    private final FacultyRepository faculty;
    private final CompanyRepository companies;
    private final PlacementDriveRepository drives;
    private final ApplicationRepository applications;

    public AdminOverviewController(UserRepository users, StudentRepository students, FacultyRepository faculty,
                                   CompanyRepository companies, PlacementDriveRepository drives,
                                   ApplicationRepository applications) {
        this.users = users;
        this.students = students;
        this.faculty = faculty;
        this.companies = companies;
        this.drives = drives;
        this.applications = applications;
    }

    @GetMapping("/overview")
    public Object overview() {
        long activeUsers = users.findAll().stream().filter(x -> x.isActive()).count();
        long selected = applications.findAll().stream()
                .filter(x -> x.getStatus().name().equals("SELECTED")).count();

        var result = new LinkedHashMap<String, Object>();
        result.put("users", users.count());
        result.put("activeUsers", activeUsers);
        result.put("students", students.count());
        result.put("faculty", faculty.count());
        result.put("companies", companies.count());
        result.put("drives", drives.count());
        result.put("applications", applications.count());
        result.put("selected", selected);
        return result;
    }

    @GetMapping("/users")
    public Object users() {
        return users.findAll().stream()
                .sorted(Comparator.comparing(com.campusconnect.entity.User::getFullName))
                .map(u -> {
                    var result = new LinkedHashMap<String, Object>();
                    result.put("id", u.getId());
                    result.put("name", u.getFullName());
                    result.put("email", u.getEmail());
                    result.put("role", u.getRole());
                    result.put("active", u.isActive());
                    return result;
                }).toList();
    }

    @PatchMapping("/users/{id}/active")
    public Object setActive(Authentication a,@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        var user = users.findById(id).orElseThrow();
        if (user.getEmail().equalsIgnoreCase(a.getName()) && !Boolean.TRUE.equals(body.get("active"))) {
            throw new IllegalArgumentException("You cannot deactivate your own admin account.");
        }
        user.setActive(Boolean.TRUE.equals(body.get("active")));
        var saved = users.save(user);

        var result = new LinkedHashMap<String, Object>();
        result.put("id", saved.getId());
        result.put("name", saved.getFullName());
        result.put("email", saved.getEmail());
        result.put("role", saved.getRole());
        result.put("active", saved.isActive());
        return result;
    }
}
