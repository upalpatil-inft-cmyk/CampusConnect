package com.campusconnect.controller;

import com.campusconnect.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/student/subjects")
public class SubjectController {
    private final UserRepository users;
    private final StudentRepository students;
    private final SubjectRepository subjects;
    private final AttendanceRepository attendance;
    private final MarkRepository marks;

    public SubjectController(UserRepository users, StudentRepository students,
                             SubjectRepository subjects, AttendanceRepository attendance,
                             MarkRepository marks){
        this.users=users;
        this.students=students;
        this.subjects=subjects;
        this.attendance=attendance;
        this.marks=marks;
    }

    @GetMapping
    public Object mySubjects(Authentication a){
        var student=students.findByUser(users.findByEmail(a.getName()).orElseThrow()).orElseThrow();

        return subjects.findAll().stream()
                .filter(s -> s.getDepartment().getId().equals(student.getDepartment().getId())
                        && s.getSemester()==student.getSemester())
                .map(s -> {
                    var records=attendance.findByStudent(student).stream()
                            .filter(x -> x.getSubject().getId().equals(s.getId())).toList();
                    var present=records.stream().filter(x -> x.isPresent()).count();
                    var attendancePct=records.isEmpty()?0:Math.round((present*100.0)/records.size());

                    var subjectMarks=marks.findByStudent(student).stream()
                            .filter(x -> x.getSubject().getId().equals(s.getId()))
                            .findFirst();

                    return Map.of(
                            "id",s.getId(),
                            "name",s.getName(),
                            "code",s.getCode(),
                            "credits",5,
                            "attendance",attendancePct,
                            "internalMarks",subjectMarks.map(x -> x.getInternalMarks()).orElse(0),
                            "totalMarks",subjectMarks.map(x -> x.getTotalMarks()).orElse(0)
                    );
                }).toList();
    }
}
