package com.campusconnect.dto;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
public record DriveRequest(
    @NotNull Long companyId,
    @NotBlank @Size(max=200) String jobRole,
    @Positive @DecimalMax("1000") double packageLpa,
    @PositiveOrZero @DecimalMax("10") double minimumCgpa,
    @NotNull LocalDate deadline,
    @Size(max=1000) String eligibleBranches
) {}