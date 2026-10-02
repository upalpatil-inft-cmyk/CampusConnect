package com.campusconnect.controller;

import com.campusconnect.entity.Faculty;
import com.campusconnect.repository.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/faculty")
@PreAuthorize("hasRole('FACULTY')")
public class FacultyOverviewController {
    private final UserRepository users;
    private final FacultyRepository faculty;
    private final StudentRepository students;
    private final SubjectRepository subjects;
    private final AssignmentRepository assignments;
    private final SubmissionRepository submissions;

    public FacultyOverviewController(UserRepository users, FacultyRepository faculty, StudentRepository students,
                                     SubjectRepository subjects, AssignmentRepository assignments,
                                     SubmissionRepository submissions) {
        this.users = users;
        this.faculty = faculty;
        this.students = students;
        this.subjects = subjects;
        this.assignments = assignments;
        this.submissions = submissions;
    }

    private Faculty currentFaculty(Authentication a) {
        return faculty.findByUser(users.findByEmail(a.getName()).orElseThrow()).orElseThrow();
    }

    @GetMapping("/overview")
    public Object overview(Authentication a) {
        var f = currentFaculty(a);
        var dept = f.getDepartment();
        var studentCount = students.findAll().stream()
                .filter(s -> s.getDepartment().getId().equals(dept.getId())).count();
        var subjectList = subjects.findAll().stream()
                .filter(s -> s.getDepartment().getId().equals(dept.getId())).toList();
        var assignmentList = assignments.findAll().stream()
                .filter(x -> x.getFaculty().getId().equals(f.getId())).toList();
        var assignmentIds = assignmentList.stream().map(x -> x.getId()).toSet();
        var pending = submissions.findAll().stream()
                .filter(x -> assignmentIds.contains(x.getAssignment().getId()) && x.getMarks() == null).count();

        return Map.of(
                "faculty", f.getUser().getFullName(),
                "department", dept.getName(),
                "departmentCode", dept.getCode(),
                "students", studentCount,
                "subjects", subjectList.size(),
                "assignments", assignmentList.size(),
                "pendingReviews", pending
        );
    }

    @GetMapping("/students")
    public Object students(Authentication a) {
        var f = currentFaculty(a);
        return students.findAll().stream()
                .filter(s -> s.getDepartment().getId().equals(f.getDepartment().getId()))
                .map(s -> Map.of(
                        "id", s.getId(),
                        "name", s.getUser().getFullName(),
                        "rollNumber", s.getRollNumber(),
                        "semester", s.getSemester(),
                        "cgpa", s.getCgpa()
                )).toList();
    }

    @GetMapping("/subjects")
    public Object subjects(Authentication a) {
        var f = currentFaculty(a);
        return subjects.findAll().stream()
                .filter(s -> s.getDepartment().getId().equals(f.getDepartment().getId()))
                .map(s -> Map.of(
                        "id", s.getId(),
                        "name", s.getName(),
                        "code", s.getCode(),
                        "semester", s.getSemester()
                )).toList();
    }

    @GetMapping("/assignments/mine")
    public Object myAssignments(Authentication a) {
        var f = currentFaculty(a);
        return assignments.findAll().stream()
                .filter(x -> x.getFaculty().getId().equals(f.getId()))
                .sorted(Comparator.comparing(x -> x.getDeadline()))
                .map(x -> Map.of(
                        "id", x.getId(),
                        "title", x.getTitle(),
                        "subject", x.getSubject().getCode(),
                        "deadline", x.getDeadline(),
                        "description", x.getDescription()
                )).toList();
    }
}
