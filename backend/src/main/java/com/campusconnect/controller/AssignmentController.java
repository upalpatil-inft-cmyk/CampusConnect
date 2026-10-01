package com.campusconnect.controller;

import com.campusconnect.dto.SubmissionRequest;
import com.campusconnect.entity.Submission;
import com.campusconnect.repository.*;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {
    private final AssignmentRepository assignments; private final SubmissionRepository submissions;
    private final StudentRepository students; private final UserRepository users;
    public AssignmentController(AssignmentRepository assignments,SubmissionRepository submissions,StudentRepository students,UserRepository users){
        this.assignments=assignments;this.submissions=submissions;this.students=students;this.users=users;
    }

    @GetMapping public Object all(){return assignments.findAll();}

    @PostMapping("/submit")
    public Object submit(Authentication a,@Valid @RequestBody SubmissionRequest r){
        var student=students.findByUser(users.findByEmail(a.getName()).orElseThrow()).orElseThrow();
        var assignment=assignments.findById(r.assignmentId()).orElseThrow();
        return submissions.save(new Submission(assignment,student,r.fileName()));
    }
}
