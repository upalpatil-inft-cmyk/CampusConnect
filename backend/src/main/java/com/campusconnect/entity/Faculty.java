package com.campusconnect.entity;

import jakarta.persistence.*;

@Entity
public class Faculty {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @OneToOne(optional=false) @JoinColumn(name="user_id", foreignKey=@ForeignKey(name="fk_faculty_user")) private User user;
    @ManyToOne(optional=false) @JoinColumn(name="department_id", foreignKey=@ForeignKey(name="fk_faculty_department")) private Department department;
    private String employeeId;

    public Faculty() {}
    public Faculty(User user, Department department, String employeeId){
        this.user=user;this.department=department;this.employeeId=employeeId;
    }
    public Long getId(){return id;} public User getUser(){return user;} public Department getDepartment(){return department;}
    public String getEmployeeId(){return employeeId;}
}
