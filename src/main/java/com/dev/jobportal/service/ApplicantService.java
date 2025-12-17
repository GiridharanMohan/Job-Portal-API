package com.dev.jobportal.service;

import com.dev.jobportal.model.Job;
import com.dev.jobportal.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicantService {

    @Autowired
    private JobRepository jobRepository;

    public ResponseEntity<List<Job>> getAvailableJobs() {
        List<Job> allAvailableJobs = jobRepository.findAll();
        return ResponseEntity.ok(allAvailableJobs);
    }
}
