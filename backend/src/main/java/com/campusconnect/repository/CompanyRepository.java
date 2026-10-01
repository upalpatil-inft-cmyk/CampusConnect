package com.campusconnect.repository;
import com.campusconnect.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CompanyRepository extends JpaRepository<Company,Long>{}