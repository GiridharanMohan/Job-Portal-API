package com.dev.jobportal.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class JobResponseDto {
    private Long jobId;
    private String jobTitle;
    private String jobDescription;
    private String jobLocation;
    private String companyName;
    private String experienceRequired;
    private LocalDateTime postedOn;
    private String postedBy;
}
