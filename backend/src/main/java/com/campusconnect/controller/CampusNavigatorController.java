package com.campusconnect.controller;

import com.campusconnect.entity.Assignment;
import com.campusconnect.entity.Attendance;
import com.campusconnect.entity.Mark;
import com.campusconnect.entity.PlacementDrive;
import com.campusconnect.entity.Student;
import com.campusconnect.repository.ApplicationRepository;
import com.campusconnect.repository.AssignmentRepository;
import com.campusconnect.repository.AttendanceRepository;
import com.campusconnect.repository.MarkRepository;
import com.campusconnect.repository.StudentRepository;
import com.campusconnect.repository.UserRepository;
import com.campusconnect.repository.SubmissionRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@RestController
@RequestMapping("/api/student/navigator")
@PreAuthorize("hasRole('STUDENT')")
public class CampusNavigatorController {
    private final UserRepository users;
    private final StudentRepository students;
    private final AssignmentRepository assignments;
    private final SubmissionRepository submissions;
    private final AttendanceRepository attendance;
    private final MarkRepository marks;
    private final ApplicationRepository applications;
    private final com.campusconnect.repository.PlacementDriveRepository drives;

    public CampusNavigatorController(UserRepository users, StudentRepository students,
                                     AssignmentRepository assignments, SubmissionRepository submissions,
                                     AttendanceRepository attendance, MarkRepository marks,
                                     ApplicationRepository applications,
                                     com.campusconnect.repository.PlacementDriveRepository drives) {
        this.users = users;
        this.students = students;
        this.assignments = assignments;
        this.submissions = submissions;
        this.attendance = attendance;
        this.marks = marks;
        this.applications = applications;
        this.drives = drives;
    }

    @GetMapping
    public Object navigator(Authentication authentication) {
        Student student = students.findByUser(
                users.findByEmail(authentication.getName()).orElseThrow()
        ).orElseThrow();

        LocalDate today = LocalDate.now();
        List<Map<String, Object>> recommendations = new ArrayList<>();

        var studentSubmissions = submissions.findByStudent(student);
        Set<Long> submittedAssignmentIds = new HashSet<>();
        studentSubmissions.forEach(s -> submittedAssignmentIds.add(s.getAssignment().getId()));

        List<Assignment> currentAssignments = assignments.findAll().stream()
                .filter(a -> a.getSubject().getDepartment().getId().equals(student.getDepartment().getId()))
                .filter(a -> a.getSubject().getSemester() == student.getSemester())
                .toList();

        for (Assignment assignment : currentAssignments) {
            long days = ChronoUnit.DAYS.between(today, assignment.getDeadline());
            if (!submittedAssignmentIds.contains(assignment.getId())) {
                String priority = days < 0 || days <= 1 ? "HIGH" : days <= 3 ? "MEDIUM" : "LOW";
                String dueText = days < 0 ? "overdue" : days == 0 ? "due today" : days == 1 ? "due tomorrow" : "due in " + days + " days";
                recommendations.add(item(
                        "ASSIGNMENT", priority,
                        "Submit " + assignment.getTitle(),
                        assignment.getSubject().getCode() + " · " + dueText,
                        "Finish and upload the assignment before the deadline.",
                        "Open assignment", "/assignments", assignment.getDeadline()
                ));
            }
        }

        List<PlacementDrive> eligibleDrives = drives.findAll().stream()
                .filter(d -> !d.getDeadline().isBefore(today))
                .filter(d -> isEligible(student, d))
                .toList();

        var myApplications = applications.findByStudent(student);
        Set<Long> appliedDriveIds = new HashSet<>();
        myApplications.forEach(a -> appliedDriveIds.add(a.getPlacementDrive().getId()));

        for (PlacementDrive drive : eligibleDrives) {
            if (!appliedDriveIds.contains(drive.getId())) {
                long days = ChronoUnit.DAYS.between(today, drive.getDeadline());
                String priority = days <= 2 ? "HIGH" : days <= 5 ? "MEDIUM" : "LOW";
                String deadlineText = days == 0 ? "closes today" : days == 1 ? "closes tomorrow" : "closes in " + days + " days";
                recommendations.add(item(
                        "PLACEMENT", priority,
                        "Apply to " + drive.getCompany().getName(),
                        drive.getJobRole() + " · " + drive.getPackageLpa() + " LPA · " + deadlineText,
                        "You meet the current CGPA and branch eligibility rules.",
                        "View placement", "/placements", drive.getDeadline()
                ));
            }
        }

        List<Attendance> studentAttendance = attendance.findByStudent(student);
        int present = (int) studentAttendance.stream().filter(Attendance::isPresent).count();
        int recorded = studentAttendance.size();
        double attendancePct = recorded > 0 ? (present * 100.0) / recorded : 100.0;
        if (recorded > 0 && attendancePct < 75) {
            recommendations.add(item(
                    "ATTENDANCE", "HIGH",
                    "Attendance needs attention",
                    String.format(Locale.ROOT, "%.1f%% overall attendance", attendancePct),
                    "Reach 75% or higher to keep your academic record healthy.",
                    "View attendance", "/attendance", null
            ));
        } else if (recorded > 0 && attendancePct < 80) {
            recommendations.add(item(
                    "ATTENDANCE", "MEDIUM",
                    "Protect your attendance",
                    String.format(Locale.ROOT, "%.1f%% overall attendance", attendancePct),
                    "A few missed classes could put you below the safer range.",
                    "View attendance", "/attendance", null
            ));
        }

        List<Mark> studentMarks = marks.findByStudent(student);
        studentMarks.stream()
                .filter(m -> m.getSubject().getDepartment().getId().equals(student.getDepartment().getId()))
                .filter(m -> m.getSubject().getSemester() == student.getSemester())
                .filter(m -> m.getTotalMarks() > 0)
                .map(m -> new Object[]{m, (m.getInternalMarks() / m.getTotalMarks()) * 100.0})
                .filter(x -> (double) x[1] < 60)
                .sorted(Comparator.comparingDouble(x -> (double) x[1]))
                .limit(2)
                .forEach(x -> {
                    Mark m = (Mark) x[0];
                    double pct = (double) x[1];
                    recommendations.add(item(
                            "ACADEMICS", pct < 50 ? "HIGH" : "MEDIUM",
                            "Improve " + m.getSubject().getName(),
                            String.format(Locale.ROOT, "%.1f%% in the latest internal assessment", pct),
                            "Use the subject resources and focus on the next assessment.",
                            "View performance", "/performance", null
                    ));
                });

        boolean profileIncomplete = isBlank(student.getPhone()) || isBlank(student.getGithubUrl()) || isBlank(student.getLinkedinUrl());
        if (profileIncomplete) {
            recommendations.add(item(
                    "PROFILE", "LOW",
                    "Complete your profile",
                    "Some profile details are still missing",
                    "A complete profile helps faculty and placement workflows use accurate information.",
                    "Update profile", "/profile", null
            ));
        }

        recommendations.sort(Comparator
                .comparingInt((Map<String, Object> x) -> priorityRank((String) x.get("priority")))
                .thenComparing(x -> String.valueOf(x.get("dueDate")), Comparator.nullsLast(Comparator.naturalOrder())));

        List<Map<String, Object>> finalRecommendations = recommendations.size() > 6
                ? new ArrayList<>(recommendations.subList(0, 6))
                : recommendations;

        int readiness = readinessScore(student, attendancePct, recorded, studentMarks, eligibleDrives, appliedDriveIds, profileIncomplete, currentAssignments, submittedAssignmentIds);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("readinessScore", readiness);
        response.put("summary", finalRecommendations.isEmpty()
                ? "You're in a strong position. No urgent campus actions need your attention."
                : "You have " + finalRecommendations.size() + " action" + (finalRecommendations.size() == 1 ? "" : "s") + " worth reviewing.");
        response.put("recommendations", finalRecommendations);
        response.put("eligibleDrives", eligibleDrives.size());
        response.put("pendingAssignments", currentAssignments.stream().filter(a -> !submittedAssignmentIds.contains(a.getId())).count());
        response.put("attendancePercentage", Math.round(attendancePct * 10.0) / 10.0);
        return response;
    }

    private boolean isEligible(Student student, PlacementDrive drive) {
        boolean cgpa = student.getCgpa() >= drive.getMinimumCgpa();
        String branches = drive.getEligibleBranches();
        if (branches == null || branches.isBlank()) return cgpa;
        String code = student.getDepartment().getCode();
        String name = student.getDepartment().getName();
        boolean branch = Arrays.stream(branches.split(","))
                .map(String::trim)
                .anyMatch(x -> x.equalsIgnoreCase(code) || x.equalsIgnoreCase(name));
        return cgpa && branch;
    }

    private int readinessScore(Student student, double attendancePct, int recorded,
                               List<Mark> marks, List<PlacementDrive> eligibleDrives,
                               Set<Long> appliedDriveIds, boolean profileIncomplete,
                               List<Assignment> assignments, Set<Long> submittedAssignmentIds) {
        int score = 100;
        if (recorded > 0 && attendancePct < 75) score -= 20;
        else if (recorded > 0 && attendancePct < 80) score -= 10;

        List<Mark> currentMarks = marks.stream()
                .filter(m -> m.getSubject().getDepartment().getId().equals(student.getDepartment().getId()))
                .filter(m -> m.getSubject().getSemester() == student.getSemester())
                .toList();
        if (!currentMarks.isEmpty()) {
            double avg = currentMarks.stream()
                    .filter(m -> m.getTotalMarks() > 0)
                    .mapToDouble(m -> (m.getInternalMarks() / m.getTotalMarks()) * 100.0)
                    .average().orElse(100);
            if (avg < 60) score -= 15;
            else if (avg < 70) score -= 8;
        }

        long pending = assignments.stream().filter(a -> !submittedAssignmentIds.contains(a.getId())).count();
        if (pending >= 3) score -= 10;
        else if (pending > 0) score -= 5;

        if (profileIncomplete) score -= 5;
        if (!eligibleDrives.isEmpty() && eligibleDrives.stream().noneMatch(d -> appliedDriveIds.contains(d.getId()))) score -= 5;

        return Math.max(0, Math.min(100, score));
    }

    private Map<String, Object> item(String type, String priority, String title, String reason,
                                     String detail, String actionLabel, String actionRoute, LocalDate dueDate) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("type", type);
        item.put("priority", priority);
        item.put("title", title);
        item.put("reason", reason);
        item.put("detail", detail);
        item.put("actionLabel", actionLabel);
        item.put("actionRoute", actionRoute);
        item.put("dueDate", dueDate);
        return item;
    }

    private int priorityRank(String priority) {
        return switch (priority) {
            case "HIGH" -> 0;
            case "MEDIUM" -> 1;
            default -> 2;
        };
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
