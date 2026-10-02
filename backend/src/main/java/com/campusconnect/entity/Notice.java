package com.campusconnect.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Notice {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false) private String title;
    @Column(nullable=false,length=3000) private String content;
    @Column(nullable=false) private LocalDateTime publishedAt=LocalDateTime.now();
    @ManyToOne(optional=false) @JoinColumn(name="published_by_id", foreignKey=@ForeignKey(name="fk_notice_published_by")) private User publishedBy;

    public Notice() {}
    public Notice(String title,String content,User publishedBy){this.title=title;this.content=content;this.publishedBy=publishedBy;}
    public Long getId(){return id;} public String getTitle(){return title;} public String getContent(){return content;}
    public LocalDateTime getPublishedAt(){return publishedAt;} public User getPublishedBy(){return publishedBy;}
}
