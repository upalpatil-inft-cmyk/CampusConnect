package com.campusconnect.controller;

import com.campusconnect.dto.*;
import com.campusconnect.entity.*;
import com.campusconnect.repository.*;
import com.campusconnect.service.NotificationService;
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
    private final AssignmentRepository assignments; private final NotificationService notificationService;

    public FacultyController(UserRepository users,FacultyRepository faculty,StudentRepository students,SubjectRepository subjects,
                             AttendanceRepository attendance,MarkRepository marks,AssignmentRepository assignments,
                             NotificationService notificationService){
        this.users=users;this.faculty=faculty;this.students=students;this.subjects=subjects;this.attendance=attendance;this.marks=marks;
        this.assignments=assignments;this.notificationService=notificationService;
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
        if (r.internalMarks() > r.totalMarks()) {
            throw new IllegalArgumentException("Marks obtained cannot exceed total marks.");
        }
        if (r.totalMarks() > 1000) {
            throw new IllegalArgumentException("Total marks are too high.");
        }
        var saved=marks.findByStudentAndSubject(s,sub);
        if(saved==null) saved=new Mark(s,sub,r.internalMarks(),r.totalMarks()); else saved.update(r.internalMarks(),r.totalMarks());
        saved=marks.save(saved);
        return Map.of("id",saved.getId(),"studentId",s.getId(),"student",s.getUser().getFullName(),
                "subject",sub.getName(),"internalMarks",saved.getInternalMarks(),"totalMarks",saved.getTotalMarks());
    }

    @PostMapping("/assignments")
    public Object createAssignment(Authentication a,@Valid @RequestBody AssignmentRequest r){
        var f=currentFaculty(a);
        var sub=subjects.findById(r.subjectId()).orElseThrow();
        if (r.deadline().isBefore(java.time.LocalDate.now())) {
            throw new IllegalArgumentException("Assignment deadline cannot be in the past.");
        }
        if (!sub.getDepartment().getId().equals(f.getDepartment().getId())) {
            throw new IllegalArgumentException("You can only publish assignments for your department.");
        }
        var saved=assignments.save(new Assignment(r.title(),r.description(),sub,f,r.deadline()));
        notificationService.createForStudents(
            NotificationType.ASSIGNMENT,
            "New assignment posted",
            saved.getTitle()+" · due "+saved.getDeadline(),
            "/assignments",
            "ASSIGNMENT:"+saved.getId()
        );
        return saved;
    }
}
