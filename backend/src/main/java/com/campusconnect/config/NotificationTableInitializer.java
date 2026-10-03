package com.campusconnect.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class NotificationTableInitializer {
    private final JdbcTemplate jdbc;

    public NotificationTableInitializer(JdbcTemplate jdbc){this.jdbc=jdbc;}

    @EventListener(ApplicationReadyEvent.class)
    public void ensureTable(){
        jdbc.execute("""
            CREATE TABLE IF NOT EXISTS notifications (
                id BIGINT NOT NULL AUTO_INCREMENT,
                user_id BIGINT NOT NULL,
                type VARCHAR(32) NOT NULL,
                title VARCHAR(255) NOT NULL,
                message VARCHAR(1000) NOT NULL,
                link VARCHAR(255) NOT NULL,
                dedupe_key VARCHAR(180) NOT NULL,
                created_at DATETIME(6) NOT NULL,
                read_at DATETIME(6) NULL,
                PRIMARY KEY (id),
                UNIQUE KEY uk_notification_dedupe (dedupe_key),
                KEY idx_notification_user_created (user_id, created_at)
            )
        """);
    }
}
