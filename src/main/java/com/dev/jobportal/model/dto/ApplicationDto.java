package com.dev.jobportal.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class ApplicationDto {

    private Long applicationId;
    private Long jobId;
    private String jobTitle;
    private Long applicantId;
    private String applicantName;
    private String resumeFileName;
    private byte[] resume;
    private String applicationStatus;
    private LocalDate appliedOn;
}
