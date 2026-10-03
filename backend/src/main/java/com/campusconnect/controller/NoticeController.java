package com.campusconnect.controller;

import com.campusconnect.dto.NoticeRequest;
import com.campusconnect.entity.Notice;
import com.campusconnect.repository.*;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/notices")
public class NoticeController {
    private final NoticeRepository notices; private final UserRepository users;
    public NoticeController(NoticeRepository notices,UserRepository users){this.notices=notices;this.users=users;}
    @GetMapping public Object all(){return notices.findAll();}
    @PostMapping
    @PreAuthorize("hasAnyRole('FACULTY','ADMIN')")
    public Object create(Authentication a,@Valid @RequestBody NoticeRequest r){
        var user=users.findByEmail(a.getName()).orElseThrow();
        return notices.save(new Notice(r.title(),r.content(),user));
    }
}
