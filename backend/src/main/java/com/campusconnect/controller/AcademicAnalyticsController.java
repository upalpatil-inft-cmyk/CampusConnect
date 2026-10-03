package com.campusconnect.controller;

import com.campusconnect.entity.Attendance;
import com.campusconnect.entity.Mark;
import com.campusconnect.entity.Student;
import com.campusconnect.entity.Subject;
import com.campusconnect.repository.AttendanceRepository;
import com.campusconnect.repository.MarkRepository;
import com.campusconnect.repository.StudentRepository;
import com.campusconnect.repository.SubjectRepository;
import com.campusconnect.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/student/analytics")
public class AcademicAnalyticsController {
    private final UserRepository users;
    private final StudentRepository students;
    private final SubjectRepository subjects;
    private final MarkRepository marks;
    private final AttendanceRepository attendance;

    public AcademicAnalyticsController(UserRepository users, StudentRepository students,
                                       SubjectRepository subjects, MarkRepository marks,
                                       AttendanceRepository attendance) {
        this.users = users;
        this.students = students;
        this.subjects = subjects;
        this.marks = marks;
        this.attendance = attendance;
    }

    @GetMapping
    public Object analytics(Authentication authentication) {
        Student student = students.findByUser(
                users.findByEmail(authentication.getName()).orElseThrow()
        ).orElseThrow();

        List<Subject> currentSubjects = subjects.findAll().stream()
                .filter(s -> s.getDepartment().getId().equals(student.getDepartment().getId()))
                .filter(s -> s.getSemester() == student.getSemester())
                .sorted(Comparator.comparing(Subject::getCode))
                .toList();

        List<Mark> studentMarks = marks.findByStudent(student);
        List<Attendance> studentAttendance = attendance.findByStudent(student);

        Map<Long, Mark> markBySubject = studentMarks.stream()
                .collect(Collectors.toMap(m -> m.getSubject().getId(), m -> m, (a, b) -> b));

        Map<Long, List<Attendance>> attendanceBySubject = studentAttendance.stream()
                .collect(Collectors.groupingBy(a -> a.getSubject().getId()));

        double totalObtained = 0;
        double totalPossible = 0;
        int present = 0;
        int recorded = 0;
        List<Map<String, Object>> subjectRows = new ArrayList<>();

        for (Subject subject : currentSubjects) {
            Mark mark = markBySubject.get(subject.getId());
            List<Attendance> attendanceRows = attendanceBySubject.getOrDefault(subject.getId(), List.of());

            double obtained = mark == null ? 0 : mark.getInternalMarks();
            double possible = mark == null ? 0 : mark.getTotalMarks();
            int subjectPresent = (int) attendanceRows.stream().filter(Attendance::isPresent).count();
            int subjectRecorded = attendanceRows.size();

            totalObtained += obtained;
            totalPossible += possible;
            present += subjectPresent;
            recorded += subjectRecorded;

            double percentage = possible > 0 ? (obtained / possible) * 100 : 0;
            double attendancePercentage = subjectRecorded > 0 ? ((double) subjectPresent / subjectRecorded) * 100 : 0;

            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", subject.getId());
            row.put("code", subject.getCode());
            row.put("name", subject.getName());
            row.put("marks", obtained);
            row.put("totalMarks", possible);
            row.put("percentage", Math.round(percentage * 10.0) / 10.0);
            row.put("grade", grade(percentage));
            row.put("attendance", Math.round(attendancePercentage * 10.0) / 10.0);
            row.put("attendancePresent", subjectPresent);
            row.put("attendanceTotal", subjectRecorded);
            subjectRows.add(row);
        }

        double marksPercentage = totalPossible > 0 ? (totalObtained / totalPossible) * 100 : 0;
        double attendancePercentage = recorded > 0 ? ((double) present / recorded) * 100 : 0;

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("semester", student.getSemester());
        response.put("cgpa", student.getCgpa());
        response.put("subjects", subjectRows.size());
        response.put("marksPercentage", Math.round(marksPercentage * 10.0) / 10.0);
        response.put("totalMarks", Math.round(totalObtained * 10.0) / 10.0);
        response.put("possibleMarks", Math.round(totalPossible * 10.0) / 10.0);
        response.put("attendancePercentage", Math.round(attendancePercentage * 10.0) / 10.0);
        response.put("presentClasses", present);
        response.put("recordedClasses", recorded);
        response.put("subjectPerformance", subjectRows);
        return response;
    }

    private String grade(double percentage) {
        if (percentage >= 90) return "A+";
        if (percentage >= 80) return "A";
        if (percentage >= 70) return "B";
        if (percentage >= 60) return "C";
        if (percentage >= 50) return "D";
        return "Needs attention";
    }
}
