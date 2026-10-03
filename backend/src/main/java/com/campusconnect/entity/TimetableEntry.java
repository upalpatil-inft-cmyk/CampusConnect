package com.campusconnect.entity;

import jakarta.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Table(name="student_timetable")
public class TimetableEntry {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String subjectName;

    @Column(nullable=false)
    private String subjectCode;

    @Column(nullable=false)
    private String facultyName;

    @Column(nullable=false)
    private String departmentCode;

    @Column(nullable=false)
    private int semester;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private DayOfWeek dayOfWeek;

    @Column(nullable=false)
    private LocalTime startTime;

    @Column(nullable=false)
    private LocalTime endTime;

    @Column(nullable=false)
    private String room;

    public TimetableEntry() {}

    public TimetableEntry(String subjectName, String subjectCode, String facultyName,
                          String departmentCode, int semester, DayOfWeek dayOfWeek,
                          LocalTime startTime, LocalTime endTime, String room) {
        this.subjectName=subjectName;
        this.subjectCode=subjectCode;
        this.facultyName=facultyName;
        this.departmentCode=departmentCode;
        this.semester=semester;
        this.dayOfWeek=dayOfWeek;
        this.startTime=startTime;
        this.endTime=endTime;
        this.room=room;
    }

    public Long getId(){return id;}
    public String getSubjectName(){return subjectName;}
    public String getSubjectCode(){return subjectCode;}
    public String getFacultyName(){return facultyName;}
    public String getDepartmentCode(){return departmentCode;}
    public int getSemester(){return semester;}
    public DayOfWeek getDayOfWeek(){return dayOfWeek;}
    public LocalTime getStartTime(){return startTime;}
    public LocalTime getEndTime(){return endTime;}
    public String getRoom(){return room;}
}
