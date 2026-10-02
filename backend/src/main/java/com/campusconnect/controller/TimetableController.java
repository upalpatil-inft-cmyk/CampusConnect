package com.campusconnect.controller;

import com.campusconnect.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/student/timetable")
public class TimetableController {
    private final UserRepository users;
    private final StudentRepository students;
    private final TimetableEntryRepository timetable;

    public TimetableController(UserRepository users, StudentRepository students,
                               TimetableEntryRepository timetable){
        this.users=users;
        this.students=students;
        this.timetable=timetable;
    }

    @GetMapping
    public Object myTimetable(Authentication a){
        var user=users.findByEmail(a.getName()).orElseThrow();
        var student=students.findByUser(user).orElseThrow();

        return timetable.findBySubject_DepartmentAndSubject_SemesterOrderByDayOfWeekAscStartTimeAsc(
                student.getDepartment(), student.getSemester()
        ).stream().map(x -> Map.of(
                "id", x.getId(),
                "day", x.getDayOfWeek().name(),
                "startTime", x.getStartTime().toString(),
                "endTime", x.getEndTime().toString(),
                "subject", x.getSubject().getName(),
                "code", x.getSubject().getCode(),
                "faculty", x.getFaculty().getUser().getFullName(),
                "room", x.getRoom()
        )).toList();
    }
}
