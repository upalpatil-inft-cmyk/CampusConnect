package com.campusconnect.dto;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
public record AttendanceRequest(Long studentId,Long subjectId,@NotNull LocalDate date,boolean present){}
