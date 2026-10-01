package com.campusconnect.dto;
import jakarta.validation.constraints.NotBlank;
public record NoticeRequest(@NotBlank String title,@NotBlank String content){}
