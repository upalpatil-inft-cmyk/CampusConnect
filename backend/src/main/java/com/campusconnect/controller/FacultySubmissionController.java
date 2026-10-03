package com.campusconnect.controller;

import com.campusconnect.repository.SubmissionRepository;
import com.campusconnect.repository.UserRepository;
import com.campusconnect.repository.FacultyRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/faculty/submissions")
@PreAuthorize("hasRole('FACULTY')")
public class FacultySubmissionController {
    private final SubmissionRepository submissions;
    private final UserRepository users;
    private final FacultyRepository faculty;
    public FacultySubmissionController(SubmissionRepository submissions,UserRepository users,FacultyRepository faculty){
        this.submissions=submissions;this.users=users;this.faculty=faculty;
    }

    @GetMapping
    public Object all(Authentication a){
        var f=faculty.findByUser(users.findByEmail(a.getName()).orElseThrow()).orElseThrow();
        return submissions.findAll().stream()
            .filter(s -> s.getAssignment().getFaculty().getId().equals(f.getId()))
            .map(s->Map.of(
            "id",s.getId(),"assignmentId",s.getAssignment().getId(),"assignment",s.getAssignment().getTitle(),
            "studentId",s.getStudent().getId(),"student",s.getStudent().getUser().getFullName(),
            "fileName",s.getFileName(),"submittedAt",s.getSubmittedAt(),
            "marks",s.getMarks()==null?"—":s.getMarks(),"feedback",s.getFeedback()==null?"—":s.getFeedback()
        )).toList();
    }

    @PatchMapping("/{id}/grade")
    public Object grade(Authentication a,@PathVariable Long id,@RequestBody Map<String,Object> body){
        var f=faculty.findByUser(users.findByEmail(a.getName()).orElseThrow()).orElseThrow();
        var submission=submissions.findById(id).orElseThrow();
        if (!submission.getAssignment().getFaculty().getId().equals(f.getId())) {
            throw new IllegalArgumentException("You can only grade submissions for your assignments.");
        }
        Double marks=body.get("marks")==null?null:Double.valueOf(body.get("marks").toString());
        String feedback=body.get("feedback")==null?"":body.get("feedback").toString();
        if(marks!=null && (marks<0 || marks>100)) throw new IllegalArgumentException("Marks must be between 0 and 100.");
        submission.grade(marks,feedback);
        return submissions.save(submission);
    }
}