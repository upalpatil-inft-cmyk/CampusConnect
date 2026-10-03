package com.campusconnect.controller;

import com.campusconnect.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;

@RestController
@RequestMapping("/api/student/timetable")
public class TimetableController {
    private final UserRepository users;
    private final StudentRepository students;
    private final TimetableEntryRepository timetable;
    private final SubjectRepository subjects;
    private final FacultyRepository faculty;

    public TimetableController(UserRepository users, StudentRepository students,
                               TimetableEntryRepository timetable, SubjectRepository subjects,
                               FacultyRepository faculty){
        this.users=users;
        this.students=students;
        this.timetable=timetable;
        this.subjects=subjects;
        this.faculty=faculty;
    }

    @GetMapping
    public Object myTimetable(Authentication a){
        var user=users.findByEmail(a.getName()).orElseThrow();
        var student=students.findByUser(user).orElseThrow();

        var stored=timetable.findBySubject_DepartmentAndSubject_SemesterOrderByDayOfWeekAscStartTimeAsc(
                student.getDepartment(), student.getSemester()
        );

        if(!stored.isEmpty()){
            return stored.stream().map(x -> Map.of(
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

        var java=subjects.findAll().stream().filter(s -> "CS301".equals(s.getCode())
                && s.getDepartment().getId().equals(student.getDepartment().getId())
                && s.getSemester()==student.getSemester()).findFirst().orElse(null);
        var dbms=subjects.findAll().stream().filter(s -> "CS302".equals(s.getCode())
                && s.getDepartment().getId().equals(student.getDepartment().getId())
                && s.getSemester()==student.getSemester()).findFirst().orElse(null);
        var f=faculty.findAll().stream().filter(x -> x.getDepartment().getId().equals(student.getDepartment().getId()))
                .findFirst().orElse(null);

        if(java==null || dbms==null || f==null) return List.of();

        var result=new ArrayList<Map<String,Object>>();
        result.add(entry(1,DayOfWeek.MONDAY,LocalTime.of(9,0),LocalTime.of(10,0),java,f,"Lab 3"));
        result.add(entry(2,DayOfWeek.MONDAY,LocalTime.of(10,15),LocalTime.of(11,15),dbms,f,"Room 204"));
        result.add(entry(3,DayOfWeek.WEDNESDAY,LocalTime.of(11,30),LocalTime.of(12,30),java,f,"Room 204"));
        result.add(entry(4,DayOfWeek.THURSDAY,LocalTime.of(9,0),LocalTime.of(10,0),dbms,f,"Lab 2"));
        result.add(entry(5,DayOfWeek.FRIDAY,LocalTime.of(10,15),LocalTime.of(11,15),java,f,"Room 204"));
        return result;
    }

    private Map<String,Object> entry(long id, DayOfWeek day, LocalTime start, LocalTime end,
                                      com.campusconnect.entity.Subject subject,
                                      com.campusconnect.entity.Faculty faculty, String room){
        return Map.of(
                "id", id,
                "day", day.name(),
                "startTime", start.toString(),
                "endTime", end.toString(),
                "subject", subject.getName(),
                "code", subject.getCode(),
                "faculty", faculty.getUser().getFullName(),
                "room", room
        );
    }
}
