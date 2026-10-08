package com.campusconnect.controller;

import com.campusconnect.entity.Faculty;
import com.campusconnect.entity.Subject;
import com.campusconnect.entity.TimetableEntry;
import com.campusconnect.repository.FacultyRepository;
import com.campusconnect.repository.SubjectRepository;
import com.campusconnect.repository.TimetableEntryRepository;
import com.campusconnect.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.Map;

@RestController
@RequestMapping("/api/faculty/timetable")
@PreAuthorize("hasRole('FACULTY')")
public class FacultyTimetableController {
    private final UserRepository users;
    private final FacultyRepository faculty;
    private final SubjectRepository subjects;
    private final TimetableEntryRepository timetable;

    public FacultyTimetableController(UserRepository users, FacultyRepository faculty,
                                      SubjectRepository subjects, TimetableEntryRepository timetable) {
        this.users = users;
        this.faculty = faculty;
        this.subjects = subjects;
        this.timetable = timetable;
    }

    private Faculty currentFaculty(Authentication a) {
        return faculty.findByUser(users.findByEmail(a.getName()).orElseThrow()).orElseThrow();
    }

    @GetMapping
    public Object list(@RequestParam(required = false) Integer semester, Authentication a) {
        var f = currentFaculty(a);
        var rows = semester == null
                ? timetable.findByDepartmentCodeOrderByDayOfWeekAscStartTimeAsc(f.getDepartment().getCode())
                : timetable.findByDepartmentCodeAndSemesterOrderByDayOfWeekAscStartTimeAsc(
                        f.getDepartment().getCode(), semester);

        return rows.stream().map(this::toMap).toList();
    }

    @PostMapping
    public Object create(@RequestBody Map<String, Object> body, Authentication a) {
        var f = currentFaculty(a);
        var entry = buildEntry(body, f, null);
        return toMap(timetable.save(entry));
    }

    @PutMapping("/{id}")
    public Object update(@PathVariable Long id, @RequestBody Map<String, Object> body, Authentication a) {
        var f = currentFaculty(a);
        var existing = timetable.findById(id).orElseThrow();
        if (!existing.getDepartmentCode().equals(f.getDepartment().getCode())) {
            throw new IllegalArgumentException("You can only update timetable entries from your department.");
        }
        var entry = buildEntry(body, f, existing);
        return toMap(timetable.save(entry));
    }

    @DeleteMapping("/{id}")
    public Object delete(@PathVariable Long id, Authentication a) {
        var f = currentFaculty(a);
        var existing = timetable.findById(id).orElseThrow();
        if (!existing.getDepartmentCode().equals(f.getDepartment().getCode())) {
            throw new IllegalArgumentException("You can only delete timetable entries from your department.");
        }
        timetable.delete(existing);
        return Map.of("message", "Timetable entry deleted.");
    }

    private TimetableEntry buildEntry(Map<String, Object> body, Faculty f, TimetableEntry existing) {
        long subjectId = longValue(body, "subjectId");
        int semester = intValue(body, "semester");
        String day = text(body, "day");
        LocalTime start = LocalTime.parse(text(body, "startTime"));
        LocalTime end = LocalTime.parse(text(body, "endTime"));
        String room = text(body, "room");

        if (semester < 1 || semester > 12) throw new IllegalArgumentException("Semester must be between 1 and 12.");
        if (end.compareTo(start) <= 0) throw new IllegalArgumentException("End time must be after start time.");
        DayOfWeek dayOfWeek;
        try {
            dayOfWeek = DayOfWeek.valueOf(day.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid timetable day.");
        }
        if (room.length() > 80) throw new IllegalArgumentException("Room is too long.");

        Subject subject = subjects.findById(subjectId).orElseThrow();
        if (!subject.getDepartment().getId().equals(f.getDepartment().getId())) {
            throw new IllegalArgumentException("You can only schedule subjects from your department.");
        }
        if (subject.getSemester() != semester) {
            throw new IllegalArgumentException("Selected subject belongs to semester " + subject.getSemester() + ".");
        }

        if (existing == null) {
            return new TimetableEntry(subject.getName(), subject.getCode(), f.getUser().getFullName(),
                    f.getDepartment().getCode(), semester, dayOfWeek, start, end, room);
        }

        existing.setSubjectName(subject.getName());
        existing.setSubjectCode(subject.getCode());
        existing.setFacultyName(f.getUser().getFullName());
        existing.setDepartmentCode(f.getDepartment().getCode());
        existing.setSemester(semester);
        existing.setDayOfWeek(dayOfWeek);
        existing.setStartTime(start);
        existing.setEndTime(end);
        existing.setRoom(room);
        return existing;
    }

    private Map<String, Object> toMap(TimetableEntry x) {
        return Map.of(
                "id", x.getId(),
                "day", x.getDayOfWeek().name(),
                "startTime", x.getStartTime().toString(),
                "endTime", x.getEndTime().toString(),
                "subject", x.getSubjectName(),
                "code", x.getSubjectCode(),
                "faculty", x.getFacultyName(),
                "semester", x.getSemester(),
                "room", x.getRoom()
        );
    }

    private String text(Map<String, Object> body, String key) {
        Object value = body.get(key);
        if (value == null || value.toString().trim().isEmpty()) throw new IllegalArgumentException(key + " is required.");
        return value.toString().trim();
    }

    private int intValue(Map<String, Object> body, String key) {
        Object value = body.get(key);
        if (value == null) throw new IllegalArgumentException(key + " is required.");
        try { return Integer.parseInt(value.toString()); }
        catch (Exception e) { throw new IllegalArgumentException("Invalid " + key + "."); }
    }

    private long longValue(Map<String, Object> body, String key) {
        Object value = body.get(key);
        if (value == null) throw new IllegalArgumentException(key + " is required.");
        try { return Long.parseLong(value.toString()); }
        catch (Exception e) { throw new IllegalArgumentException("Invalid " + key + "."); }
    }
}
