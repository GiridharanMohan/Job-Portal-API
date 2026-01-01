package com.dev.jobportal.service;

import com.dev.jobportal.model.Applicant;
import com.dev.jobportal.model.Application;
import com.dev.jobportal.model.Job;
import com.dev.jobportal.model.User;
import com.dev.jobportal.model.dto.JobResponseDto;
import com.dev.jobportal.repository.ApplicantRepository;
import com.dev.jobportal.repository.ApplicationRepository;
import com.dev.jobportal.repository.JobRepository;
import com.dev.jobportal.util.Constants;
import com.dev.jobportal.util.JwtUtil;
import com.dev.jobportal.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
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

    @Autowired
    private Util util;

    public ResponseEntity<List<JobResponseDto>> getAvailableJobs() {
        List<JobResponseDto> allAvailableJobs = jobRepository.findAll().stream()
                .map(job -> util.toJobResponseDto(job)).toList();
        return ResponseEntity.ok(allAvailableJobs);
    }

    public ResponseEntity<String> applyForJob(Long jobId) {
        User user = jwtUtil.getUserFromToken();
        Applicant applicant = applicantRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Applicant not found for user ID: " + user.getId()));
        Job job = jobRepository.findById(jobId).orElseThrow(() -> new RuntimeException("Invalid job ID"));
        if (hasAlreadyApplied(applicant, job)) {
            return ResponseEntity.ok("You have already applied for this job");
        }
        if (applicant.getResume() != null) {
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

    private boolean hasAlreadyApplied(Applicant applicant, Job job) {
        return applicationRepository
                .findByApplicantIdAndJobId(applicant.getId(), job.getId())
                .isPresent();
    }

    public ResponseEntity<String> uploadResume(MultipartFile resumeFile) {
        log.info("Beginning of uploadResume");
        User user = jwtUtil.getUserFromToken();
        Applicant existingApplicant = applicantRepository.findByUserId(user.getId())
                .orElseThrow(() -> new UsernameNotFoundException("User with ID: "+user.getId()+" not found"));
        try {
            if (!resumeFile.isEmpty()) {
                log.info("setting resume to their respective fields");
                existingApplicant.setFileName(resumeFile.getOriginalFilename());
                existingApplicant.setFileType(resumeFile.getContentType());
                existingApplicant.setResume(resumeFile.getBytes());
                applicantRepository.save(existingApplicant);
                return ResponseEntity.ok("Resume uploaded successfully");
            }
        } catch (IOException e){
            return ResponseEntity.unprocessableContent().body("Error in getting resume");
        }
        return ResponseEntity.badRequest().body("Resume should not be empty");
    }
}
