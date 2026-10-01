package com.campusconnect.repository;
import com.campusconnect.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
public interface NoticeRepository extends JpaRepository<Notice,Long>{}