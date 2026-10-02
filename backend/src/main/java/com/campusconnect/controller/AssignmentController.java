package com.campusconnect.controller;

import com.campusconnect.dto.SubmissionRequest;
import com.campusconnect.entity.Submission;
import com.campusconnect.repository.*;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {
    private final AssignmentRepository assignments; private final SubmissionRepository submissions;
    private final StudentRepository students; private final UserRepository users;
    public AssignmentController(AssignmentRepository assignments,SubmissionRepository submissions,StudentRepository students,UserRepository users){
        this.assignments=assignments;this.submissions=submissions;this.students=students;this.users=users;
    }

    @GetMapping public Object all(){return assignments.findAll();}

    @GetMapping("/student-submissions")
    @PreAuthorize("hasRole('STUDENT')")
    public Object mySubmissions(Authentication a){
        var student=students.findByUser(users.findByEmail(a.getName()).orElseThrow()).orElseThrow();
        return submissions.findByStudent(student).stream().map(s->Map.of(
            "id",s.getId(),"assignment",s.getAssignment().getTitle(),"assignmentId",s.getAssignment().getId(),
            "fileName",s.getFileName(),"submittedAt",s.getSubmittedAt(),
            "marks",s.getMarks()==null?"—":s.getMarks(),"feedback",s.getFeedback()==null?"—":s.getFeedback()
        )).toList();
    }

    @PostMapping(value="/submit-file", consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('STUDENT')")
    public Object submitFile(Authentication a,@RequestParam Long assignmentId,@RequestPart("file") MultipartFile file) throws Exception{
        if(file.isEmpty()) throw new IllegalArgumentException("Please choose a file.");
        if(file.getSize()>5_000_000) throw new IllegalArgumentException("File must be 5 MB or smaller.");
        var student=students.findByUser(users.findByEmail(a.getName()).orElseThrow()).orElseThrow();
        var assignment=assignments.findById(assignmentId).orElseThrow();
        var existing=submissions.findByStudent(student).stream().filter(s->s.getAssignment().getId().equals(assignmentId)).findFirst();
        var submission=existing.orElseGet(()->new Submission(assignment,student,file.getOriginalFilename()));
        submission.attachFile(file.getBytes(),file.getContentType());
        submission = submissions.save(submission);
        return Map.of("id",submission.getId(),"fileName",submission.getFileName(),"submittedAt",submission.getSubmittedAt());
    }

    @PostMapping("/submit")
    @PreAuthorize("hasRole('STUDENT')")
    public Object submit(Authentication a,@Valid @RequestBody SubmissionRequest r){
        var student=students.findByUser(users.findByEmail(a.getName()).orElseThrow()).orElseThrow();
        var assignment=assignments.findById(r.assignmentId()).orElseThrow();
        return submissions.save(new Submission(assignment,student,r.fileName()));
    }
}