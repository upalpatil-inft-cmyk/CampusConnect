package com.campusconnect.dto;
import jakarta.validation.constraints.*;
public record MarkRequest(
    @NotNull Long studentId,
    @NotNull Long subjectId,
    @PositiveOrZero double internalMarks,
    @Positive double totalMarks
) {}