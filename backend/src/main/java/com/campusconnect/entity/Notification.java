package com.campusconnect.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="notifications", indexes={
    @Index(name="idx_notification_user_created", columnList="user_id,createdAt")
})
public class Notification {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false)
    @JoinColumn(name="user_id", foreignKey=@ForeignKey(name="fk_notification_user"))
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private NotificationType type;

    @Column(nullable=false)
    private String title;

    @Column(nullable=false, length=1000)
    private String message;

    @Column(nullable=false, length=255)
    private String link;

    @Column(nullable=false, unique=true, length=180)
    private String dedupeKey;

    @Column(nullable=false)
    private LocalDateTime createdAt=LocalDateTime.now();

    private LocalDateTime readAt;

    public Notification() {}

    public Notification(User user, NotificationType type, String title, String message, String link, String dedupeKey) {
        this.user=user; this.type=type; this.title=title; this.message=message;
        this.link=link; this.dedupeKey=dedupeKey;
    }

    public Long getId(){return id;}
    public User getUser(){return user;}
    public NotificationType getType(){return type;}
    public String getTitle(){return title;}
    public String getMessage(){return message;}
    public String getLink(){return link;}
    public String getDedupeKey(){return dedupeKey;}
    public LocalDateTime getCreatedAt(){return createdAt;}
    public LocalDateTime getReadAt(){return readAt;}
    public void markRead(){this.readAt=LocalDateTime.now();}
}
