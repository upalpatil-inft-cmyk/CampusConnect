package com.campusconnect.dto;
import jakarta.validation.constraints.*;
public record CompanyRequest(
    @NotBlank @Size(max=200) String name,
    @Size(max=500) String website,
    @Size(max=1500) String description
) {}