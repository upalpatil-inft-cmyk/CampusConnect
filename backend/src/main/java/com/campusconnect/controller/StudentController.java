package com.campusconnect.controller;

import com.campusconnect.repository.*;
import com.campusconnect.dto.ProfileUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/student")
public class StudentController {
    private final UserRepository users;
    private final StudentRepository students;
    private final AttendanceRepository attendance;
    private final MarkRepository marks;
    private final ApplicationRepository applications;
    private final SubmissionRepository submissions;

    public StudentController(UserRepository users,StudentRepository students,AttendanceRepository attendance,
                             MarkRepository marks,ApplicationRepository applications,SubmissionRepository submissions){
        this.users=users;this.students=students;this.attendance=attendance;this.marks=marks;this.applications=applications;this.submissions=submissions;
    }

    @GetMapping("/me")
    public Object me(Authentication a){
        var user=users.findByEmail(a.getName()).orElseThrow();
        var student=students.findByUser(user).orElseThrow();
        return Map.of("name",user.getFullName(),"email",user.getEmail(),"rollNumber",student.getRollNumber(),
                "semester",student.getSemester(),"cgpa",student.getCgpa(),"department",student.getDepartment().getName(),
                "phone",student.getPhone()==null?"":student.getPhone(),"githubUrl",student.getGithubUrl()==null?"":student.getGithubUrl(),
                "linkedinUrl",student.getLinkedinUrl()==null?"":student.getLinkedinUrl());
    }

    @PutMapping("/me")
    public Object updateMe(Authentication a, @Valid @RequestBody ProfileUpdateRequest r){
        var user=users.findByEmail(a.getName()).orElseThrow();
        var student=students.findByUser(user).orElseThrow();
        user.setFullName(r.fullName());
        student.setPhone(r.phone());
        student.setGithubUrl(r.githubUrl());
        student.setLinkedinUrl(r.linkedinUrl());
        users.save(user); students.save(student);
        return Map.of("name",user.getFullName(),"email",user.getEmail(),"rollNumber",student.getRollNumber(),
                "semester",student.getSemester(),"cgpa",student.getCgpa(),"department",student.getDepartment().getName(),
                "phone",student.getPhone()==null?"":student.getPhone(),"githubUrl",student.getGithubUrl()==null?"":student.getGithubUrl(),
                "linkedinUrl",student.getLinkedinUrl()==null?"":student.getLinkedinUrl());
    }

    @GetMapping("/attendance")
    public Object attendance(Authentication a){
        var student=students.findByUser(users.findByEmail(a.getName()).orElseThrow()).orElseThrow();
        return attendance.findByStudent(student).stream().map(x->Map.of(
                "subject",x.getSubject().getName(),"date",x.getDate(),"present",x.isPresent())).toList();
    }

    @GetMapping("/marks")
    public Object marks(Authentication a){
        var student=students.findByUser(users.findByEmail(a.getName()).orElseThrow()).orElseThrow();
        return marks.findByStudent(student).stream().map(x->Map.of(
                "subject",x.getSubject().getName(),"internalMarks",x.getInternalMarks(),"totalMarks",x.getTotalMarks())).toList();
    }

    @GetMapping("/applications")
    public Object applications(Authentication a){
        var student=students.findByUser(users.findByEmail(a.getName()).orElseThrow()).orElseThrow();
        return applications.findByStudent(student).stream().map(x->Map.of(
                "id",x.getId(),"company",x.getPlacementDrive().getCompany().getName(),
                "role",x.getPlacementDrive().getJobRole(),"status",x.getStatus().name())).toList();
    }

    @GetMapping("/submissions")
    public Object submissions(Authentication a){
        var student=students.findByUser(users.findByEmail(a.getName()).orElseThrow()).orElseThrow();
        return submissions.findByStudent(student).stream().map(x->Map.of(
                "assignment",x.getAssignment().getTitle(),"fileName",x.getFileName(),
                "submittedAt",x.getSubmittedAt(),"marks",x.getMarks()==null?"":x.getMarks(),
                "feedback",x.getFeedback()==null?"":x.getFeedback())).toList();
    }
}
