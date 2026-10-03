package com.campusconnect.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class TimetableSeeder {
    // Timetable rows are served by TimetableController when storage is empty.
    // Keeping startup writes out of the critical boot path avoids MySQL 9 FK/system-schema
    // locking issues while preserving the timetable feature for students.
}
