package com.dev.jobportal.service;

import com.dev.jobportal.model.Applicant;
import com.dev.jobportal.model.Application;
import com.dev.jobportal.model.Job;
import com.dev.jobportal.model.User;
import com.dev.jobportal.repository.ApplicantRepository;
import com.dev.jobportal.repository.ApplicationRepository;
import com.dev.jobportal.repository.JobRepository;
import com.dev.jobportal.util.Constants;
import com.dev.jobportal.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
        if(hasAlreadyApplied(applicant, job)){
            return ResponseEntity.ok("You have already applied for this job");
        }
        if(applicant.getResume() != null){
            Application application = new Application();
            application.setApplicant(applicant);
            application.setJob(job);
            application.setAppliedOn(LocalDate.now());
            application.setStatus(Constants.STATUS_APPLIED);
            applicationRepository.save(application);
            return ResponseEntity.ok("Applied successfully");
        }
        return ResponseEntity.ok("Resume not found! Please upload your resume to apply for the job");
    }

    private boolean hasAlreadyApplied(Applicant applicant, Job job){
        return applicationRepository
                .findByApplicantIdAndJobId(applicant.getId(), job.getId())
                .isPresent();
    }

    public ResponseEntity<String> uploadResume(Applicant applicant) {
        User user = jwtUtil.getUserFromToken();
        Optional<Applicant> existingApplicant = applicantRepository.findByUserId(user.getId());
        if(existingApplicant.isPresent()) {
            existingApplicant.get().setResume(applicant.getResume());
            applicantRepository.save(existingApplicant.get());
            return ResponseEntity.ok("Resume uploaded successfully");
        }
        return ResponseEntity.badRequest().body("User not found");
    }
}
