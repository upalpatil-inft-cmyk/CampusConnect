package com.campusconnect.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public User() {}
    public User(String email, String password, Role role, String fullName) {
        this.email = email; this.password = password; this.role = role; this.fullName = fullName;
    }
    public Long getId(){return id;} public String getEmail(){return email;} public String getPassword(){return password;}
    public Role getRole(){return role;} public String getFullName(){return fullName;} public boolean isActive(){return active;}
    public void setPassword(String password){this.password=password;} public void setFullName(String fullName){this.fullName=fullName;}
}
