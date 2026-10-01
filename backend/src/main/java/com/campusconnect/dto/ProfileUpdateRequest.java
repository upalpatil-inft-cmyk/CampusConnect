package com.campusconnect.dto;
import jakarta.validation.constraints.NotBlank;
public record ProfileUpdateRequest(@NotBlank String fullName, String phone, String githubUrl, String linkedinUrl) {}
