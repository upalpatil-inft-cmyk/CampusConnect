package com.campusconnect.entity;

import jakarta.persistence.*;

@Entity
public class Company {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false, unique=true) private String name;
    private String website;
    @Column(length=1500) private String description;

    public Company() {}
    public Company(String name,String website,String description){this.name=name;this.website=website;this.description=description;}
    public Long getId(){return id;} public String getName(){return name;} public String getWebsite(){return website;}
    public String getDescription(){return description;}
}
