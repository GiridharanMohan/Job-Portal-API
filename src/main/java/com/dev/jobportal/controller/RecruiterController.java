package com.dev.jobportal.controller;

import com.dev.jobportal.model.Job;
import com.dev.jobportal.model.dto.JobResponseDto;
import com.dev.jobportal.model.dto.PaginatedResponse;
import com.dev.jobportal.service.RecruiterService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/recruiter")
public class RecruiterController {

    @Autowired
    private RecruiterService recruiterService;

    @PostMapping("/post-job")
    public ResponseEntity<String> postJob(@RequestBody @Valid Job job) {
        log.info("Processing job posting request");
        return recruiterService.postJob(job);
    }

    @GetMapping("/posted-jobs")
    public ResponseEntity<PaginatedResponse> getPostedJobs(@RequestParam(name = "pageNumber") int pageNumber, @RequestParam(name = "size") int pageSize) {
        log.info("Fetching jobs posted by the user");
        return recruiterService.getPostedJobs(pageNumber, pageSize);
    }

    @GetMapping("/posted-jobs/{id}")
    public ResponseEntity<JobResponseDto> getPostedJobById(@PathVariable Long id) {
        log.info("Processing request to fetch Job with ID: {}", id);
        return recruiterService.getPostedJobById(id);
    }

    @PutMapping("/close-job/{id}")
    public ResponseEntity<String> closeHiring(@PathVariable Long id) {
        log.info("Processing request to set the Job status as CLOSED, Job ID: {}", id);
        return recruiterService.closeHiring(id);
    }

    @GetMapping("/allApplicants")
    public ResponseEntity<PaginatedResponse> getAllApplicationsForJobId(@RequestParam Long id, @RequestParam String jobTitle,
                                                                                   @RequestParam(name = "pageNumber") int pageNumber,
                                                                                   @RequestParam(name = "size") int pageSize) {
        log.info("Processing request to get all applications for the Job ID: {}", id);
        return recruiterService.getAllApplicationsForJobId(id, jobTitle, pageNumber, pageSize);
    }

    @GetMapping("/{id}/resume")
    public ResponseEntity<byte[]> getResumeByApplicationNumber(@PathVariable("id") Long applicationId){
        log.info("Fetching resume of the Application ID: {}", applicationId);
        return recruiterService.getResume(applicationId);
    }

    @PutMapping("application/{id}/status")
    public ResponseEntity<String> changeApplicationStatus(@RequestPart String status, @PathVariable("id") Long applicationId){
        log.info("Processing the request to change the application status, Application ID: {}, status: {}", applicationId, status);
        return recruiterService.changeApplicationStatus(status, applicationId);
    }
}
