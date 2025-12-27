package com.dev.jobportal.controller;

import com.dev.jobportal.model.Applicant;
import com.dev.jobportal.model.Job;
import com.dev.jobportal.model.dto.JobResponseDto;
import com.dev.jobportal.service.ApplicantService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ApplicantController {

    @Autowired
    private ApplicantService applicantService;

    @GetMapping("/jobs")
    public ResponseEntity<List<JobResponseDto>> getAvailableJobs() {
        return applicantService.getAvailableJobs();
    }

    @PostMapping("/apply")
    public ResponseEntity<String> applyForJob(@RequestParam Long jobId){
        return applicantService.applyForJob(jobId);
    }

    @PostMapping("/upload-resume")
    public ResponseEntity<String> uploadResume(@Valid @RequestBody Applicant applicant) {
        return applicantService.uploadResume(applicant);
    }
}
