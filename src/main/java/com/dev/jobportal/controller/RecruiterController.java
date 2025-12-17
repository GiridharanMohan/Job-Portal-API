package com.dev.jobportal.controller;

import com.dev.jobportal.model.Applicant;
import com.dev.jobportal.model.Application;
import com.dev.jobportal.model.Job;
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
    public ResponseEntity<?> postJob(@RequestBody @Valid Job job) {
        return recruiterService.postJob(job);
    }

    @GetMapping("/posted-jobs")
    public ResponseEntity<List<Job>> getPostedJobs() {
        return recruiterService.getPostedJobs();
    }

    @GetMapping("/posted-jobs/{id}")
    public ResponseEntity<Job> getPostedJobById(@PathVariable Long id) {
        return recruiterService.getPostedJobById(id);
    }

    @DeleteMapping("delete-job/{id}")
    public ResponseEntity<String> deleteJobById(@PathVariable Long id) {
        return recruiterService.deleteJobById(id);
    }

    @GetMapping("/allApplicants")
    public ResponseEntity<List<Application>> getAllApplicationsForJobId(@RequestParam Long id, @RequestParam String jobTitle) {
        return recruiterService.getAllApplicationsForJobId(id, jobTitle);
    }
}
