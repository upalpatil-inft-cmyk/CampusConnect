package com.campusconnect.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Assignment {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false) private String title;
    @Column(nullable=false, length=3000) private String description;
    @ManyToOne(optional=false) private Subject subject;
    @ManyToOne(optional=false) private Faculty faculty;
    @Column(nullable=false) private LocalDate deadline;

    public Assignment() {}
    public Assignment(String title,String description,Subject subject,Faculty faculty,LocalDate deadline){
        this.title=title;this.description=description;this.subject=subject;this.faculty=faculty;this.deadline=deadline;
    }
    public Long getId(){return id;} public String getTitle(){return title;} public String getDescription(){return description;}
    public Subject getSubject(){return subject;} public Faculty getFaculty(){return faculty;} public LocalDate getDeadline(){return deadline;}
}
