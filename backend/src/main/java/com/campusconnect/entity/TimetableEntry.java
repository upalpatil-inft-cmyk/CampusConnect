package com.campusconnect.entity;

import jakarta.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Table(name="timetable_entries")
public class TimetableEntry {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false)
    @JoinColumn(name="subject_id", foreignKey=@ForeignKey(name="fk_timetable_subject"))
    private Subject subject;

    @ManyToOne(optional=false)
    @JoinColumn(name="faculty_id", foreignKey=@ForeignKey(name="fk_timetable_faculty"))
    private Faculty faculty;

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

    public TimetableEntry(Subject subject, Faculty faculty, DayOfWeek dayOfWeek,
                          LocalTime startTime, LocalTime endTime, String room) {
        this.subject=subject;
        this.faculty=faculty;
        this.dayOfWeek=dayOfWeek;
        this.startTime=startTime;
        this.endTime=endTime;
        this.room=room;
    }

    public Long getId(){return id;}
    public Subject getSubject(){return subject;}
    public Faculty getFaculty(){return faculty;}
    public DayOfWeek getDayOfWeek(){return dayOfWeek;}
    public LocalTime getStartTime(){return startTime;}
    public LocalTime getEndTime(){return endTime;}
    public String getRoom(){return room;}
}
