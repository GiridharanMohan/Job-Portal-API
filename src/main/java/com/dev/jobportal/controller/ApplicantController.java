package com.dev.jobportal.controller;

import com.dev.jobportal.model.dto.JobResponseDto;
import com.dev.jobportal.service.ApplicantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
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
        log.info("Applying for job with ID: {}", jobId);
        return applicantService.applyForJob(jobId);
    }

    @PostMapping(value = "/upload-resume", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadResume(@RequestPart("file") MultipartFile resumeFile) {
        log.info("Uploading resume with file name: {}", resumeFile.getOriginalFilename());
        return applicantService.uploadResume(resumeFile);
    }
}
