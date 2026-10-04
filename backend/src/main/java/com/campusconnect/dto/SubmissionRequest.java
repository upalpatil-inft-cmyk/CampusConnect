package com.campusconnect.dto;
import jakarta.validation.constraints.*;
public record SubmissionRequest(
    @NotNull Long assignmentId,
    @NotBlank @Size(max=255) String fileName
) {}