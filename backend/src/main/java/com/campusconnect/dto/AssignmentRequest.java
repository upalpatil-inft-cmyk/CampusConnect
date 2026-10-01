package com.campusconnect.dto;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
public record AssignmentRequest(@NotBlank String title,@NotBlank String description,Long subjectId,@NotNull LocalDate deadline){}
