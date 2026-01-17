package com.dev.jobportal.service;

import com.dev.jobportal.model.Application;
import com.dev.jobportal.model.Job;
import com.dev.jobportal.model.User;
import com.dev.jobportal.model.dto.ApplicationResponseDto;
import com.dev.jobportal.model.dto.JobResponseDto;
import com.dev.jobportal.repository.ApplicationRepository;
import com.dev.jobportal.repository.JobRepository;
import com.dev.jobportal.util.Constant;
import com.dev.jobportal.util.JwtUtil;
import com.dev.jobportal.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RecruiterService {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private Util util;

    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<String> postJob(Job job) {
        User user = jwtUtil.getUserFromToken();
        job.setPostedBy(user);
        job.setPostedOn(LocalDateTime.now());
        job.setJobStatus(Constant.STATUS_OPEN);
        jobRepository.save(job);
        return ResponseEntity.ok("Job posted successfully");
    }

    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<List<JobResponseDto>> getPostedJobs() {
        User user = jwtUtil.getUserFromToken();
        List<JobResponseDto> jobsPostedByUser = jobRepository.findByPostedBy(user).stream()
                .map(job -> util.toJobResponseDto(job)).toList();
        return ResponseEntity.status(HttpStatus.OK).body(jobsPostedByUser);
    }

    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<JobResponseDto> getPostedJobById(Long id) {
        User user = jwtUtil.getUserFromToken();
        Optional<Job> job = jobRepository.findById(id);
        if (job.isPresent() && job.get().getPostedBy().getEmail().equals(user.getEmail())) {
            JobResponseDto jobResponse = util.toJobResponseDto(job.get());
            return ResponseEntity.ok(jobResponse);
        }
        return ResponseEntity.badRequest().build();
    }

    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<String> closeHiring(Long id) {
        User user = jwtUtil.getUserFromToken();
        Optional<Job> job = jobRepository.findById(id);
        if (job.isPresent() && job.get().getPostedBy().equals(user)) {
            Job newJob = job.get();
            newJob.setJobStatus(Constant.STATUS_CLOSED);
            newJob.setUpdateOn(LocalDateTime.now());
            jobRepository.save(newJob);
            return ResponseEntity.status(HttpStatus.OK).body("Job has been closed");
        }
        return ResponseEntity.badRequest().body("Invalid Job ID");
    }

    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<List<ApplicationResponseDto>> getAllApplicationsForJobId(Long id, String jobTitle) {
        User user = jwtUtil.getUserFromToken();
        Optional<Job> job = jobRepository.findById(id);
        if (job.isPresent() && job.get().getPostedBy().getEmail().equals(user.getEmail()) && job.get().getJobTitle().equals(jobTitle)) {
            List<ApplicationResponseDto> listOfApplications = applicationRepository.findAllByJob(job.get())
                    .stream().map(application -> util.toApplicationResponseDto(application)).toList();
            return ResponseEntity.ok(listOfApplications);
        }
        return ResponseEntity.badRequest().build();
    }

    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<byte[]> getResume(Long applicationId){
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("No application found"));
        byte[] resume = application.getApplicant().getResume();
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(application.getApplicant().getFileType()))
                .body(resume);
    }

    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<String> changeApplicationStatus(String status, Long applicationId) {
        User user = jwtUtil.getUserFromToken();
        Application applicationEntity = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if(!applicationEntity.getJob().getPostedBy().equals(user)){
            return ResponseEntity.badRequest().body("unauthorized user");
        }

        String currentStatus = applicationEntity.getStatus();
        if(currentStatus.equalsIgnoreCase(Constant.STATUS_REJECTED) || currentStatus.equalsIgnoreCase(Constant.STATUS_SHORTLISTED)) {
            return ResponseEntity.ok("This application is "+currentStatus+" already");
        }

        applicationEntity.setStatus(status.toUpperCase());
        applicationEntity.setUpdatedOn(LocalDateTime.now());
        applicationRepository.save(applicationEntity);
        return ResponseEntity.ok("Status has been changed to "+status);
    }
}
