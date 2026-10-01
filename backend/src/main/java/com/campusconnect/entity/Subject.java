package com.campusconnect.entity;

import jakarta.persistence.*;

@Entity
public class Subject {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false) private String name;
    @Column(nullable=false, unique=true) private String code;
    @ManyToOne(optional=false) private Department department;
    private int semester;

    public Subject() {}
    public Subject(String name,String code,Department department,int semester){
        this.name=name;this.code=code;this.department=department;this.semester=semester;
    }
    public Long getId(){return id;} public String getName(){return name;} public String getCode(){return code;}
    public Department getDepartment(){return department;} public int getSemester(){return semester;}
}
