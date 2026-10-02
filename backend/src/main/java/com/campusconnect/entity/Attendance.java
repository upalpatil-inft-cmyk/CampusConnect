package com.campusconnect.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"student_id","subject_id","date"}))
public class Attendance {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional=false) @JoinColumn(name="student_id", foreignKey=@ForeignKey(name="fk_attendance_student")) private Student student;
    @ManyToOne(optional=false) @JoinColumn(name="subject_id", foreignKey=@ForeignKey(name="fk_attendance_subject")) private Subject subject;
    @Column(nullable=false) private LocalDate date;
    @Column(nullable=false) private boolean present;

    public Attendance() {}
    public Attendance(Student s,Subject sub,LocalDate d,boolean p){student=s;subject=sub;date=d;present=p;}
    public Long getId(){return id;} public Student getStudent(){return student;} public Subject getSubject(){return subject;}
    public LocalDate getDate(){return date;} public boolean isPresent(){return present;}
}
