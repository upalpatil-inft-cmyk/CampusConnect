package com.campusconnect.config;

import com.campusconnect.entity.*;
import com.campusconnect.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Configuration
public class DataSeeder {
    @Bean CommandLineRunner seed(UserRepository users,DepartmentRepository departments,StudentRepository students,
                                 FacultyRepository faculty,SubjectRepository subjects,CompanyRepository companies,
                                 PlacementDriveRepository drives,AttendanceRepository attendance,MarkRepository marks,
                                 AssignmentRepository assignments,NoticeRepository notices,PasswordEncoder encoder) {
        return args -> {
            if(users.count()>0) return;

            var cs=departments.save(new Department("Computer Science","CS"));
            var admin=users.save(new User("admin@campusconnect.local",encoder.encode("Admin@123"),Role.ADMIN,"Campus Admin"));
            var fuser=users.save(new User("faculty@campusconnect.local",encoder.encode("Faculty@123"),Role.FACULTY,"Priya Sharma"));
            var suser=users.save(new User("student@campusconnect.local",encoder.encode("Student@123"),Role.STUDENT,"Upal Patil"));

            var f=faculty.save(new Faculty(fuser,cs,"FAC001"));
            var s=students.save(new Student(suser,cs,"127",5,8.4));

            var java=subjects.save(new Subject("Java Programming","CS301",cs,5));
            var dbms=subjects.save(new Subject("Database Management Systems","CS302",cs,5));

            attendance.save(new Attendance(s,java,LocalDate.now().minusDays(2),true));
            attendance.save(new Attendance(s,java,LocalDate.now().minusDays(1),true));
            attendance.save(new Attendance(s,dbms,LocalDate.now().minusDays(2),false));
            marks.save(new Mark(s,java,42,50));
            marks.save(new Mark(s,dbms,45,50));

            assignments.save(new Assignment("Spring Boot REST API","Build a small REST API using Spring Boot.",java,f,LocalDate.now().plusDays(7)));

            var company=companies.findAll().stream()
                    .filter(c -> "TCS".equalsIgnoreCase(c.getName()))
                    .findFirst()
                    .orElseGet(() -> companies.save(new Company("TCS","https://www.tcs.com","Technology and consulting company.")));
            drives.save(new PlacementDrive(company,"Graduate Engineer",7.5,7.0,LocalDate.now().plusDays(20),"CS,IT"));

            notices.save(new Notice("Welcome to CampusConnect","Tier 1 portal is now available for students and faculty.",admin));
        };
    }
}
