package com.campusconnect.controller;

import com.campusconnect.dto.NoticeRequest;
import com.campusconnect.entity.*;
import com.campusconnect.repository.*;
import com.campusconnect.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/notices")
public class NoticeController {
    private final NoticeRepository notices; private final UserRepository users; private final NotificationService notificationService;
    public NoticeController(NoticeRepository notices,UserRepository users,NotificationService notificationService){
        this.notices=notices;this.users=users;this.notificationService=notificationService;
    }
    @GetMapping public Object all(){return notices.findAll();}
    @PostMapping
    @PreAuthorize("hasAnyRole('FACULTY','ADMIN')")
    public Object create(Authentication a,@Valid @RequestBody NoticeRequest r){
        var user=users.findByEmail(a.getName()).orElseThrow();
        var saved=notices.save(new Notice(r.title(),r.content(),user));
        notificationService.createForStudents(
            NotificationType.NOTICE,
            "New campus notice",
            saved.getTitle(),
            "/",
            "NOTICE:"+saved.getId()
        );
        return saved;
    }
}
