package com.campusconnect.controller;

import com.campusconnect.dto.*;
import com.campusconnect.entity.*;
import com.campusconnect.repository.*;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

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

    private Faculty currentFaculty(Authentication a) {
        return faculty.findByUser(users.findByEmail(a.getName()).orElseThrow()).orElseThrow();
    }

    private void verifyScope(Faculty f, Student s, Subject sub) {
        if (!s.getDepartment().getId().equals(f.getDepartment().getId())
                || !sub.getDepartment().getId().equals(f.getDepartment().getId())) {
            throw new IllegalArgumentException("You can only manage records within your department.");
        }
    }

    @PostMapping("/attendance")
    public Object markAttendance(Authentication a,@Valid @RequestBody AttendanceRequest r){
        var f=currentFaculty(a);
        var s=students.findById(r.studentId()).orElseThrow(); var sub=subjects.findById(r.subjectId()).orElseThrow();
        verifyScope(f,s,sub);
        var saved=attendance.save(new Attendance(s,sub,r.date(),r.present()));
        return Map.of("id",saved.getId(),"studentId",s.getId(),"student",s.getUser().getFullName(),
                "subject",sub.getName(),"date",saved.getDate(),"present",saved.isPresent());
    }

    @PostMapping("/marks")
    public Object addMark(Authentication a,@Valid @RequestBody MarkRequest r){
        var f=currentFaculty(a);
        var s=students.findById(r.studentId()).orElseThrow(); var sub=subjects.findById(r.subjectId()).orElseThrow();
        verifyScope(f,s,sub);
        var saved=marks.save(new Mark(s,sub,r.internalMarks(),r.totalMarks()));
        return Map.of("id",saved.getId(),"studentId",s.getId(),"student",s.getUser().getFullName(),
                "subject",sub.getName(),"internalMarks",saved.getInternalMarks(),"totalMarks",saved.getTotalMarks());
    }

    @PostMapping("/assignments")
    public Object createAssignment(Authentication a,@Valid @RequestBody AssignmentRequest r){
        var f=faculty.findByUser(users.findByEmail(a.getName()).orElseThrow()).orElseThrow();
        var sub=subjects.findById(r.subjectId()).orElseThrow();
        if (!sub.getDepartment().getId().equals(f.getDepartment().getId())) {
            throw new IllegalArgumentException("You can only publish assignments for your department.");
        }
        return assignments.save(new Assignment(r.title(),r.description(),sub,f,r.deadline()));
    }
}
