package com.campusconnect.dto;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
public record AttendanceRequest(
    @NotNull Long studentId,
    @NotNull Long subjectId,
    @NotNull LocalDate date,
    boolean present
) {}