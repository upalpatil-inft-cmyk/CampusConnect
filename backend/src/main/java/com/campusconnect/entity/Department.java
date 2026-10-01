package com.campusconnect.entity;

import jakarta.persistence.*;

@Entity
public class Department {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false, unique=true) private String name;
    @Column(nullable=false, unique=true) private String code;

    public Department() {}
    public Department(String name, String code){this.name=name;this.code=code;}
    public Long getId(){return id;} public String getName(){return name;} public String getCode(){return code;}
}
