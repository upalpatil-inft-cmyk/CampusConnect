package com.campusconnect.controller;

import com.campusconnect.repository.SubmissionRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/faculty/submissions")
@PreAuthorize("hasRole('FACULTY')")
public class FacultySubmissionController {
    private final SubmissionRepository submissions;
    public FacultySubmissionController(SubmissionRepository submissions){this.submissions=submissions;}

    @GetMapping
    public Object all(){
        return submissions.findAll().stream().map(s->Map.of(
            "id",s.getId(),"assignmentId",s.getAssignment().getId(),"assignment",s.getAssignment().getTitle(),
            "studentId",s.getStudent().getId(),"student",s.getStudent().getUser().getFullName(),
            "fileName",s.getFileName(),"submittedAt",s.getSubmittedAt(),
            "marks",s.getMarks()==null?"—":s.getMarks(),"feedback",s.getFeedback()==null?"—":s.getFeedback()
        )).toList();
    }

    @PatchMapping("/{id}/grade")
    public Object grade(@PathVariable Long id,@RequestBody Map<String,Object> body){
        var submission=submissions.findById(id).orElseThrow();
        Double marks=body.get("marks")==null?null:Double.valueOf(body.get("marks").toString());
        String feedback=body.get("feedback")==null?"":body.get("feedback").toString();
        if(marks!=null && (marks<0 || marks>100)) throw new IllegalArgumentException("Marks must be between 0 and 100.");
        submission.grade(marks,feedback);
        return submissions.save(submission);
    }
}