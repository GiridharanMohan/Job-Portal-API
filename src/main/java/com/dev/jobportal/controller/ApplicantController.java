package com.dev.jobportal.controller;

import com.dev.jobportal.model.Job;
import com.dev.jobportal.service.ApplicantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ApplicantController {

    @Autowired
    private ApplicantService applicantService;

    @GetMapping("/jobs")
    public ResponseEntity<List<Job>> getAvailableJobs() {
        return applicantService.getAvailableJobs();
    }
}
