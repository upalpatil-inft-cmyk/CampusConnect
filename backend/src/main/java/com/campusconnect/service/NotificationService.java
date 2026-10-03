package com.campusconnect.service;

import com.campusconnect.entity.*;
import com.campusconnect.repository.*;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository notifications;
    private final UserRepository users;

    public NotificationService(NotificationRepository notifications, UserRepository users){
        this.notifications=notifications; this.users=users;
    }

    public void create(User user, NotificationType type, String title, String message, String link, String dedupeKey){
        if(user==null || notifications.existsByDedupeKey(dedupeKey)) return;
        notifications.save(new Notification(user,type,title,message,link,dedupeKey));
    }

    public void createForStudents(NotificationType type, String title, String message, String link, String eventKey){
        List<User> students=users.findAll().stream()
                .filter(u -> u.getRole()==Role.STUDENT && u.isActive())
                .toList();
        for(User user: students){
            create(user,type,title,message,link,eventKey+":"+user.getId());
        }
    }
}
