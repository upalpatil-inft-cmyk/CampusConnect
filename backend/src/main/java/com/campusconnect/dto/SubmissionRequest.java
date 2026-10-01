package com.campusconnect.dto;
import jakarta.validation.constraints.NotBlank;
public record SubmissionRequest(Long assignmentId,@NotBlank String fileName){}
