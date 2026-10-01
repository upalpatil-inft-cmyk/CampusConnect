package com.campusconnect.dto;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
public record DriveRequest(Long companyId,@NotBlank String jobRole,@Positive double packageLpa,@PositiveOrZero double minimumCgpa,@NotNull LocalDate deadline,String eligibleBranches){}
