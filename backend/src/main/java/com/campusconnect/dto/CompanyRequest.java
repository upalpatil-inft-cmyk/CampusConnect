package com.campusconnect.dto;
import jakarta.validation.constraints.NotBlank;
public record CompanyRequest(@NotBlank String name,String website,String description){}
