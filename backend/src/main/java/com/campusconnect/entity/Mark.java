package com.campusconnect.entity;

import jakarta.persistence.*;

@Entity
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"student_id","subject_id"}))
public class Mark {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional=false) @JoinColumn(name="student_id", foreignKey=@ForeignKey(name="fk_mark_student")) private Student student;
    @ManyToOne(optional=false) @JoinColumn(name="subject_id", foreignKey=@ForeignKey(name="fk_mark_subject")) private Subject subject;
    private double internalMarks;
    private double totalMarks;

    public Mark() {}
    public Mark(Student s,Subject sub,double internal,double total){student=s;subject=sub;internalMarks=internal;totalMarks=total;}
    public Long getId(){return id;} public Student getStudent(){return student;} public Subject getSubject(){return subject;}
    public double getInternalMarks(){return internalMarks;} public double getTotalMarks(){return totalMarks;} public void update(double internal,double total){this.internalMarks=internal;this.totalMarks=total;}
}
