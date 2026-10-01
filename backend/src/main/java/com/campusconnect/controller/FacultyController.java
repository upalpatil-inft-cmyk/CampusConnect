package com.campusconnect.controller;

import com.campusconnect.dto.*;
import com.campusconnect.entity.*;
import com.campusconnect.repository.*;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/faculty")
@PreAuthorize("hasRole('FACULTY')")
public class FacultyController {
    private final UserRepository users; private final FacultyRepository faculty; private final StudentRepository students;
    private final SubjectRepository subjects; private final AttendanceRepository attendance; private final MarkRepository marks;
    private final AssignmentRepository assignments;

    public FacultyController(UserRepository users,FacultyRepository faculty,StudentRepository students,SubjectRepository subjects,
                             AttendanceRepository attendance,MarkRepository marks,AssignmentRepository assignments){
        this.users=users;this.faculty=faculty;this.students=students;this.subjects=subjects;this.attendance=attendance;this.marks=marks;this.assignments=assignments;
    }

    @PostMapping("/attendance")
    public Object markAttendance(@Valid @RequestBody AttendanceRequest r){
        var s=students.findById(r.studentId()).orElseThrow(); var sub=subjects.findById(r.subjectId()).orElseThrow();
        return attendance.save(new Attendance(s,sub,r.date(),r.present()));
    }

    @PostMapping("/marks")
    public Object addMark(@Valid @RequestBody MarkRequest r){
        var s=students.findById(r.studentId()).orElseThrow(); var sub=subjects.findById(r.subjectId()).orElseThrow();
        return marks.save(new Mark(s,sub,r.internalMarks(),r.totalMarks()));
    }

    @PostMapping("/assignments")
    public Object createAssignment(Authentication a,@Valid @RequestBody AssignmentRequest r){
        var f=faculty.findByUser(users.findByEmail(a.getName()).orElseThrow()).orElseThrow();
        var sub=subjects.findById(r.subjectId()).orElseThrow();
        return assignments.save(new Assignment(r.title(),r.description(),sub,f,r.deadline()));
    }
}
