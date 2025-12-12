package com.dev.jobportal.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "jobs")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Job title should not be empty")
    @Column(name = "title", nullable = false)
    private String JobTitle;

    @NotBlank(message = "Job description should not be empty")
    @Column(name = "description", nullable = false, length = 3000)
    private String JobDescription;

    @NotBlank(message = "Job location should not be empty")
    @Column(name = "location", nullable = false)
    private String JobLocation;

    @NotBlank(message = "Company name should not be empty")
    @Column(name = "company_name", nullable = false)
    private String companyName;

    @NotBlank(message = "Experience required should not be empty")
    @Column(name = "experience_required", nullable = false)
    private String experienceRequired;

    @Column(name = "posted_on", nullable = false)
    private LocalDateTime postedOn;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User postedBy;
}
