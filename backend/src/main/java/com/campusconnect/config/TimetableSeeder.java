package com.campusconnect.config;

import com.campusconnect.entity.*;
import com.campusconnect.repository.*;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.context.annotation.Configuration;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Configuration
public class TimetableSeeder {
    private final SubjectRepository subjects;
    private final FacultyRepository faculty;
    private final TimetableEntryRepository timetable;

    public TimetableSeeder(SubjectRepository subjects, FacultyRepository faculty,
                           TimetableEntryRepository timetable) {
        this.subjects = subjects;
        this.faculty = faculty;
        this.timetable = timetable;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void seedTimetable() {
        if (timetable.count() > 0) return;

        var java = subjects.findAll().stream()
                .filter(s -> "CS301".equals(s.getCode()))
                .findFirst().orElse(null);
        var dbms = subjects.findAll().stream()
                .filter(s -> "CS302".equals(s.getCode()))
                .findFirst().orElse(null);
        var f = faculty.findAll().stream().findFirst().orElse(null);

        if (java == null || dbms == null || f == null) return;

        timetable.save(new TimetableEntry(java, f, DayOfWeek.MONDAY,
                LocalTime.of(9, 0), LocalTime.of(10, 0), "Lab 3"));
        timetable.save(new TimetableEntry(dbms, f, DayOfWeek.MONDAY,
                LocalTime.of(10, 15), LocalTime.of(11, 15), "Room 204"));
        timetable.save(new TimetableEntry(java, f, DayOfWeek.WEDNESDAY,
                LocalTime.of(11, 30), LocalTime.of(12, 30), "Room 204"));
        timetable.save(new TimetableEntry(dbms, f, DayOfWeek.THURSDAY,
                LocalTime.of(9, 0), LocalTime.of(10, 0), "Lab 2"));
        timetable.save(new TimetableEntry(java, f, DayOfWeek.FRIDAY,
                LocalTime.of(10, 15), LocalTime.of(11, 15), "Room 204"));
    }
}
