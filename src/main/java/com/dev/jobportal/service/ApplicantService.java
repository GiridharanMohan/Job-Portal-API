package com.dev.jobportal.service;

import com.dev.jobportal.model.Applicant;
import com.dev.jobportal.model.Application;
import com.dev.jobportal.model.Job;
import com.dev.jobportal.model.User;
import com.dev.jobportal.repository.ApplicantRepository;
import com.dev.jobportal.repository.ApplicationRepository;
import com.dev.jobportal.repository.JobRepository;
import com.dev.jobportal.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ApplicantService {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ApplicantRepository applicantRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public ResponseEntity<List<Job>> getAvailableJobs() {
        List<Job> allAvailableJobs = jobRepository.findAll();
        return ResponseEntity.ok(allAvailableJobs);
    }

    public ResponseEntity<String> applyForJob(Long jobId) {
        User user = jwtUtil.getUserFromToken();
        Applicant applicant = applicantRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Applicant not found for user ID: " + user.getId()));
        Job job = jobRepository.findById(jobId).orElseThrow(() -> new RuntimeException("Invalid job ID"));
        if(applicant.getResume() != null){
            Application application = new Application();
            application.setApplicant(applicant);
            application.setJob(job);
            application.setAppliedOn(LocalDate.now());
            applicationRepository.save(application);
            return ResponseEntity.ok("Applied successfully");
        }
        return ResponseEntity.badRequest().body("Resume not found! Please upload your resume to apply for the job");
    }
}
