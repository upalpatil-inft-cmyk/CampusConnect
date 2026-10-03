package com.campusconnect.config;

import com.campusconnect.entity.*;
import com.campusconnect.repository.*;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Configuration
public class TimetableSeeder {
    private final SubjectRepository subjects;
    private final FacultyRepository faculty;
    private final TimetableEntryRepository timetable;
    private final JdbcTemplate jdbc;

    public TimetableSeeder(SubjectRepository subjects, FacultyRepository faculty,
                           TimetableEntryRepository timetable, JdbcTemplate jdbc) {
        this.subjects = subjects;
        this.faculty = faculty;
        this.timetable = timetable;
        this.jdbc = jdbc;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void seedTimetable() {
        jdbc.execute("""
            CREATE TABLE IF NOT EXISTS student_timetable (
                id BIGINT NOT NULL AUTO_INCREMENT,
                subject_name VARCHAR(255) NOT NULL,
                subject_code VARCHAR(255) NOT NULL,
                faculty_name VARCHAR(255) NOT NULL,
                department_code VARCHAR(255) NOT NULL,
                semester INT NOT NULL,
                day_of_week VARCHAR(32) NOT NULL,
                start_time TIME NOT NULL,
                end_time TIME NOT NULL,
                room VARCHAR(255) NOT NULL,
                PRIMARY KEY (id)
            )
            """);

        if (timetable.count() > 0) return;

        var java = subjects.findAll().stream()
                .filter(s -> "CS301".equals(s.getCode()))
                .findFirst().orElse(null);
        var dbms = subjects.findAll().stream()
                .filter(s -> "CS302".equals(s.getCode()))
                .findFirst().orElse(null);
        var f = faculty.findAll().stream().findFirst().orElse(null);

        if (java == null || dbms == null || f == null) return;

        var departmentCode = java.getDepartment().getCode();
        var semester = java.getSemester();
        var facultyName = f.getUser().getFullName();

        timetable.save(new TimetableEntry(java.getName(), java.getCode(), facultyName,
                departmentCode, semester, DayOfWeek.MONDAY,
                LocalTime.of(9, 0), LocalTime.of(10, 0), "Lab 3"));
        timetable.save(new TimetableEntry(dbms.getName(), dbms.getCode(), facultyName,
                departmentCode, semester, DayOfWeek.MONDAY,
                LocalTime.of(10, 15), LocalTime.of(11, 15), "Room 204"));
        timetable.save(new TimetableEntry(java.getName(), java.getCode(), facultyName,
                departmentCode, semester, DayOfWeek.WEDNESDAY,
                LocalTime.of(11, 30), LocalTime.of(12, 30), "Room 204"));
        timetable.save(new TimetableEntry(dbms.getName(), dbms.getCode(), facultyName,
                departmentCode, semester, DayOfWeek.THURSDAY,
                LocalTime.of(9, 0), LocalTime.of(10, 0), "Lab 2"));
        timetable.save(new TimetableEntry(java.getName(), java.getCode(), facultyName,
                departmentCode, semester, DayOfWeek.FRIDAY,
                LocalTime.of(10, 15), LocalTime.of(11, 15), "Room 204"));
    }
}
