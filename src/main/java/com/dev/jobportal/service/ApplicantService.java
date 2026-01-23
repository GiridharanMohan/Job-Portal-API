package com.dev.jobportal.service;

import com.dev.jobportal.model.*;
import com.dev.jobportal.model.dto.JobResponseDto;
import com.dev.jobportal.repository.ApplicantRepository;
import com.dev.jobportal.repository.ApplicationRepository;
import com.dev.jobportal.repository.JobRepository;
import com.dev.jobportal.util.Constant;
import com.dev.jobportal.util.EmailUtil;
import com.dev.jobportal.util.JwtUtil;
import com.dev.jobportal.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

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

    @Autowired
    private EmailUtil emailUtil;

    public ResponseEntity<List<JobResponseDto>> getAvailableJobs() {
        log.info("Fetching all jobs with status OPEN");
        List<JobResponseDto> allAvailableJobs = jobRepository.findAll().stream()
                .filter(job -> job.getJobStatus().equals(Constant.STATUS_OPEN))
                .map(job -> util.toJobResponseDto(job)).toList();
        return ResponseEntity.ok(allAvailableJobs);
    }

    public ResponseEntity<String> applyForJob(Long jobId) {
        Job job = jobRepository.findById(jobId).orElseThrow(() -> new RuntimeException("Invalid job ID"));

        if(job.getJobStatus().equals(Constant.STATUS_CLOSED)) {
            log.debug("Job with job ID: {} have closed hiring", jobId);
            return ResponseEntity.ok("Recruiter is not accepting any further applications");
        }

        User user = jwtUtil.getUserFromToken();
        Applicant applicant = applicantRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Applicant not found for user ID: " + user.getId()));

        if (hasAlreadyApplied(applicant, job)) {
            log.debug("Applicant with ID: {} have already applied for the Job with ID: {}", applicant.getId(), jobId);
            return ResponseEntity.ok("You have already applied for this job");
        }

        if (applicant.getResume() != null) {
            Application application = new Application();
            application.setApplicant(applicant);
            application.setJob(job);
            application.setAppliedOn(LocalDateTime.now());
            application.setStatus(Constant.STATUS_APPLIED);
            Application applicationEntity = applicationRepository.save(application);
            log.info("Applied to the Job ID:{} successfully", jobId);
            Long applicationCount = job.getTotalApplication();
            job.setTotalApplication(++applicationCount);
            jobRepository.save(job);
            emailUtil.sendEmailNotification(user.getEmail(), Constant.APPLICATION_SUBMITTED, user.getUsername(),
                    Constant.APPLICATION_SUBMITTED_SUCCESSFULLY, job.getJobTitle(), applicationEntity.getStatus());
            return ResponseEntity.ok("Applied successfully");
        }
        return ResponseEntity.ok("Resume not found! Please upload your resume to apply for the job");
    }

    public ResponseEntity<String> uploadResume(MultipartFile resumeFile) {
        log.debug("Beginning of uploadResume");
        User user = jwtUtil.getUserFromToken();
        Applicant existingApplicant = applicantRepository.findByUserId(user.getId())
                .orElseThrow(() -> new UsernameNotFoundException("User with ID: "+user.getId()+" not found"));
        try {
            if (!resumeFile.isEmpty()) {
                if(!validFileType(resumeFile)){
                    log.warn("Not a PDF file, failed to upload");
                    return ResponseEntity.badRequest().body("Not a pdf file");
                }
                existingApplicant.setFileName(resumeFile.getOriginalFilename());
                existingApplicant.setFileType(resumeFile.getContentType());
                existingApplicant.setResume(resumeFile.getBytes());
                existingApplicant.setUpdatedOn(LocalDateTime.now());
                applicantRepository.save(existingApplicant);
                log.info("Uploaded resume successfully");
                return ResponseEntity.ok("Resume uploaded successfully");
            }
        } catch (IOException e){
            log.error("IOException occurred");
            return ResponseEntity.unprocessableContent().body("Error in getting resume");
        }
        log.warn("Resume is empty");
        return ResponseEntity.badRequest().body("Resume should not be empty");
    }

    private boolean hasAlreadyApplied(Applicant applicant, Job job) {
        return applicationRepository
                .findByApplicantIdAndJobId(applicant.getId(), job.getId())
                .isPresent();
    }

    private boolean validFileType(MultipartFile file){
        return file.getContentType() != null && file.getContentType().equals(MediaType.APPLICATION_PDF_VALUE);
    }
}
