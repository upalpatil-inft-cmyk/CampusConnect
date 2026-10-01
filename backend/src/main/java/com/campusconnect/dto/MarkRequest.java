package com.campusconnect.dto;
import jakarta.validation.constraints.*;
public record MarkRequest(Long studentId,Long subjectId,@PositiveOrZero double internalMarks,@PositiveOrZero double totalMarks){}
