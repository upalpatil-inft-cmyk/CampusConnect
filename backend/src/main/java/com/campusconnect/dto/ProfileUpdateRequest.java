package com.campusconnect.dto;
import jakarta.validation.constraints.*;
public record ProfileUpdateRequest(
    @NotBlank @Size(max=150) String fullName,
    @Size(max=30) String phone,
    @Size(max=500) String githubUrl,
    @Size(max=500) String linkedinUrl
) {}