package com.dev.jobportal.controller;

import com.dev.jobportal.model.Job;
import com.dev.jobportal.model.dto.ApplicationResponseDto;
import com.dev.jobportal.model.dto.JobResponseDto;
import com.dev.jobportal.service.RecruiterService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recruiter")
public class RecruiterController {

    @Autowired
    private RecruiterService recruiterService;

    @PostMapping("/post-job")
    public ResponseEntity<String> postJob(@RequestBody @Valid Job job) {
        return recruiterService.postJob(job);
    }

    @GetMapping("/posted-jobs")
    public ResponseEntity<List<JobResponseDto>> getPostedJobs() {
        return recruiterService.getPostedJobs();
    }

    @GetMapping("/posted-jobs/{id}")
    public ResponseEntity<JobResponseDto> getPostedJobById(@PathVariable Long id) {
        return recruiterService.getPostedJobById(id);
    }

    @PutMapping("/close-job/{id}")
    public ResponseEntity<String> closeHiring(@PathVariable Long id) {
        return recruiterService.closeHiring(id);
    }

    @GetMapping("/allApplicants")
    public ResponseEntity<List<ApplicationResponseDto>> getAllApplicationsForJobId(@RequestParam Long id, @RequestParam String jobTitle) {
        return recruiterService.getAllApplicationsForJobId(id, jobTitle);
    }

    @GetMapping("/{id}/resume")
    public ResponseEntity<byte[]> getResumeByApplicationNumber(@PathVariable("id") Long applicationId){
        return recruiterService.getResume(applicationId);
    }

    @PutMapping("application/{id}/status")
    public ResponseEntity<String> changeApplicationStatus(@RequestPart String status, @PathVariable("id") Long applicationId){
        return recruiterService.changeApplicationStatus(status, applicationId);
    }
}
