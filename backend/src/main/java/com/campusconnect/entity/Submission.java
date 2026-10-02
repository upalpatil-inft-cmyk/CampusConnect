package com.campusconnect.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"assignment_id","student_id"}))
public class Submission {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional=false) @JoinColumn(name="assignment_id", foreignKey=@ForeignKey(name="fk_submission_assignment")) private Assignment assignment;
    @ManyToOne(optional=false) @JoinColumn(name="student_id", foreignKey=@ForeignKey(name="fk_submission_student")) private Student student;
    @Column(nullable=false) private String fileName;
    @Column(nullable=false) private LocalDateTime submittedAt = LocalDateTime.now();
    @Lob @Basic(fetch=FetchType.LAZY) private byte[] fileData;
    private String contentType;
    private Double marks;
    @Column(length=2000) private String feedback;

    public Submission() {}
    public Submission(Assignment a,Student s,String fileName){assignment=a;student=s;this.fileName=fileName;}
    public Long getId(){return id;} public Assignment getAssignment(){return assignment;} public Student getStudent(){return student;}
    public String getFileName(){return fileName;} public LocalDateTime getSubmittedAt(){return submittedAt;}
    public byte[] getFileData(){return fileData;} public String getContentType(){return contentType;}
    public Double getMarks(){return marks;} public String getFeedback(){return feedback;}
    public void attachFile(byte[] data,String type){this.fileData=data;this.contentType=type;}
    public void grade(Double marks,String feedback){this.marks=marks;this.feedback=feedback;}
}