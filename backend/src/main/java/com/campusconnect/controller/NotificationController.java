package com.campusconnect.controller;

import com.campusconnect.entity.*;
import com.campusconnect.repository.*;
import com.campusconnect.service.NotificationService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/notifications")
@PreAuthorize("isAuthenticated()")
public class NotificationController {
    private final NotificationRepository notifications;
    private final UserRepository users;
    private final StudentRepository students;
    private final AssignmentRepository assignments;
    private final NotificationService service;

    public NotificationController(NotificationRepository notifications, UserRepository users, StudentRepository students,
                                  AssignmentRepository assignments, NotificationService service){
        this.notifications=notifications; this.users=users; this.students=students;
        this.assignments=assignments; this.service=service;
    }

    @GetMapping
    public Object all(Authentication a){
        User user=users.findByEmail(a.getName()).orElseThrow();
        if(user.getRole()==Role.STUDENT) createDeadlineAlerts(user);
        var rows=notifications.findByUserOrderByCreatedAtDesc(user);
        var unread=notifications.countByUserAndReadAtIsNull(user);
        return Map.of(
            "unread", unread,
            "items", rows.stream().limit(20).map(n -> Map.of(
                "id",n.getId(),
                "type",n.getType().name(),
                "title",n.getTitle(),
                "message",n.getMessage(),
                "link",n.getLink(),
                "createdAt",n.getCreatedAt(),
                "read",n.getReadAt()!=null
            )).toList()
        );
    }

    @PatchMapping("/{id}/read")
    public Object markRead(Authentication a,@PathVariable Long id){
        User user=users.findByEmail(a.getName()).orElseThrow();
        var n=notifications.findById(id).orElseThrow();
        if(!n.getUser().getId().equals(user.getId())) throw new IllegalArgumentException("Notification not found.");
        n.markRead();
        notifications.save(n);
        return Map.of("id",n.getId(),"read",true);
    }

    @PostMapping("/read-all")
    public Object markAllRead(Authentication a){
        User user=users.findByEmail(a.getName()).orElseThrow();
        var rows=notifications.findByUserOrderByCreatedAtDesc(user);
        rows.stream().filter(n -> n.getReadAt()==null).forEach(Notification::markRead);
        notifications.saveAll(rows);
        return Map.of("read",true);
    }

    private void createDeadlineAlerts(User user){
        var student=students.findByUser(user).orElse(null);
        if(student==null) return;
        LocalDate today=LocalDate.now();
        assignments.findAll().stream()
            .filter(x -> !x.getDeadline().isBefore(today) && !x.getDeadline().isAfter(today.plusDays(3)))
            .filter(x -> x.getSubject().getDepartment().getId().equals(student.getDepartment().getId()))
            .forEach(x -> service.create(
                user,
                NotificationType.DEADLINE,
                "Assignment deadline approaching",
                x.getTitle()+" is due "+x.getDeadline()+".",
                "/assignments",
                "DEADLINE:"+x.getId()+":"+user.getId()
            ));
    }
}
