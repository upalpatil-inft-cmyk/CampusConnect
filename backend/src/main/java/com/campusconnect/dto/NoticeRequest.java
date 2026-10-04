package com.campusconnect.dto;
import jakarta.validation.constraints.*;
public record NoticeRequest(
    @NotBlank @Size(max=200) String title,
    @NotBlank @Size(max=3000) String content
) {}