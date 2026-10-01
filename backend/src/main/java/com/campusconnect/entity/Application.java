package com.campusconnect.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"placement_drive_id","student_id"}))
public class Application {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional=false) private PlacementDrive placementDrive;
    @ManyToOne(optional=false) private Student student;
    @Enumerated(EnumType.STRING) @Column(nullable=false)
    private ApplicationStatus status = ApplicationStatus.APPLIED;
    @Column(nullable=false) private LocalDateTime appliedAt = LocalDateTime.now();

    public Application() {}
    public Application(PlacementDrive d,Student s){placementDrive=d;student=s;}
    public Long getId(){return id;} public PlacementDrive getPlacementDrive(){return placementDrive;} public Student getStudent(){return student;}
    public ApplicationStatus getStatus(){return status;} public LocalDateTime getAppliedAt(){return appliedAt;}
    public void setStatus(ApplicationStatus status){this.status=status;}
}
