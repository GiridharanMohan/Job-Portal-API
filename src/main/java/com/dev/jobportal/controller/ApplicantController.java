package com.dev.jobportal.controller;

import com.dev.jobportal.model.dto.PaginatedJobResponse;
import com.dev.jobportal.service.ApplicantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
public class ApplicantController {

    @Autowired
    private ApplicantService applicantService;

    @GetMapping("/jobs")
    public ResponseEntity<PaginatedJobResponse> getAvailableJobs(@RequestParam(name = "pageNumber") int pageNumber, @RequestParam(name = "size") int size) {
        return applicantService.getAvailableJobs(pageNumber, size);
    }

    @PostMapping("/apply")
    public ResponseEntity<String> applyForJob(@RequestParam Long jobId){
        log.info("Processing request to apply for the job ID: {}", jobId);
        return applicantService.applyForJob(jobId);
    }

    @PostMapping(value = "/upload-resume", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadResume(@RequestPart("file") MultipartFile resumeFile) {
        log.info("Processing request to upload the resume, File name: {}", resumeFile.getOriginalFilename());
        return applicantService.uploadResume(resumeFile);
    }
}
