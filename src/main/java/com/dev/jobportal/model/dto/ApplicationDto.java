package com.dev.jobportal.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
public class ApplicationDto {

    private Long applicationId;
    private Long jobId;
    private String jobTitle;
    private UUID applicantId;
    private String applicantName;
    private String resume;
    private String applicationStatus;
    private LocalDate appliedOn;
}
