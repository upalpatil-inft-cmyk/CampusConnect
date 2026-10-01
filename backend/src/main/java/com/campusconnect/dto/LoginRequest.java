package com.campusconnect.dto;
import jakarta.validation.constraints.*;
public record LoginRequest(@Email @NotBlank String email,@NotBlank String password){}
