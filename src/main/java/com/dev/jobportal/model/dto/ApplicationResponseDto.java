package com.dev.jobportal.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ApplicationResponseDto {

    private Long applicationId;
    private Long jobId;
    private String jobTitle;
    private Long applicantId;
    private String applicantName;
    private String resumeFileName;
    private String applicationStatus;
    private LocalDateTime appliedOn;
}
