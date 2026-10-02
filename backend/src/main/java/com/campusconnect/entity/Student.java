package com.campusconnect.entity;

import jakarta.persistence.*;

@Entity
@Table(name="students")
public class Student {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @OneToOne(optional=false) @JoinColumn(name="user_id", foreignKey=@ForeignKey(name="fk_student_user")) private User user;
    @ManyToOne(optional=false) @JoinColumn(name="department_id", foreignKey=@ForeignKey(name="fk_student_department")) private Department department;
    private String rollNumber;
    private int semester;
    private String phone;
    private String githubUrl;
    private String linkedinUrl;
    private double cgpa;

    public Student() {}
    public Student(User user, Department department, String rollNumber, int semester, double cgpa){
        this.user=user;this.department=department;this.rollNumber=rollNumber;this.semester=semester;this.cgpa=cgpa;
    }
    public Long getId(){return id;} public User getUser(){return user;} public Department getDepartment(){return department;}
    public String getRollNumber(){return rollNumber;} public int getSemester(){return semester;} public String getPhone(){return phone;}
    public String getGithubUrl(){return githubUrl;} public String getLinkedinUrl(){return linkedinUrl;} public double getCgpa(){return cgpa;}
    public void setPhone(String phone){this.phone=phone;}
    public void setGithubUrl(String githubUrl){this.githubUrl=githubUrl;}
    public void setLinkedinUrl(String linkedinUrl){this.linkedinUrl=linkedinUrl;}
}
