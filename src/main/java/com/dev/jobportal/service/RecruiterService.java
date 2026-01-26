package com.dev.jobportal.service;

import com.dev.jobportal.exception.ApplicationNotFoundException;
import com.dev.jobportal.exception.JobNotFoundException;
import com.dev.jobportal.model.Application;
import com.dev.jobportal.model.Job;
import com.dev.jobportal.model.User;
import com.dev.jobportal.model.dto.ApplicationResponseDto;
import com.dev.jobportal.model.dto.JobResponseDto;
import com.dev.jobportal.model.dto.PaginatedResponse;
import com.dev.jobportal.repository.ApplicationRepository;
import com.dev.jobportal.repository.JobRepository;
import com.dev.jobportal.util.Constant;
import com.dev.jobportal.util.EmailUtil;
import com.dev.jobportal.util.JwtUtil;
import com.dev.jobportal.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
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

    @Autowired
    private EmailUtil emailUtil;

    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<String> postJob(Job job) {
        User user = jwtUtil.getUserFromToken();
        job.setPostedBy(user);
        job.setPostedOn(LocalDateTime.now());
        job.setJobStatus(Constant.STATUS_OPEN);
        job.setTotalApplication(0L);
        jobRepository.save(job);
        return ResponseEntity.ok("Job posted successfully");
    }

    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<PaginatedResponse> getPostedJobs(int pageNumber, int pageSize) {
        User user = jwtUtil.getUserFromToken();
        Pageable pageable = PageRequest.of(pageNumber-1, pageSize);
        Page<Job> pageableJobs = jobRepository.findAllByPostedBy(pageable, user);
        List<JobResponseDto> listOfJobResponseDto = pageableJobs.getContent()
                .stream()
                .map(job -> util.toJobResponseDto(job))
                .toList();
        PaginatedResponse jobs = util.convertToPaginatedResponse(listOfJobResponseDto, pageableJobs);
        return ResponseEntity.status(HttpStatus.OK).body(jobs);
    }

    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<JobResponseDto> getPostedJobById(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new JobNotFoundException("Job not found. Job ID: "+id));
        User user = jwtUtil.getUserFromToken();
        if (job.getPostedBy().getEmail().equals(user.getEmail())) {
            JobResponseDto jobResponse = util.toJobResponseDto(job);
            return ResponseEntity.ok(jobResponse);
        }
        log.info("Job ID: {} does not exists", id);
        return ResponseEntity.badRequest().build();
    }

    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<String> closeHiring(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new JobNotFoundException("Job not found. Job ID: "+id));
        User user = jwtUtil.getUserFromToken();
        if (job.getPostedBy().equals(user)) {
            job.setJobStatus(Constant.STATUS_CLOSED);
            job.setUpdateOn(LocalDateTime.now());
            jobRepository.save(job);
            return ResponseEntity.status(HttpStatus.OK).body("Job has been closed");
        }
        return ResponseEntity.badRequest().body("Invalid Job ID");
    }

    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<PaginatedResponse> getAllApplicationsForJobId(Long id, String jobTitle, int pageNumber, int pageSize) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new JobNotFoundException("Job not found. Job ID: "+id));
        User user = jwtUtil.getUserFromToken();

        if (job.getPostedBy().equals(user) && job.getJobTitle().equals(jobTitle)) {
            Pageable pageable = PageRequest.of(pageNumber-1, pageSize);
            Page<Application> pageableApplications = applicationRepository.findAllByJob(pageable, job);
            List<ApplicationResponseDto> listOfApplications = pageableApplications.getContent()
                    .stream()
                    .map(application -> util.toApplicationResponseDto(application))
                    .toList();
            PaginatedResponse response = util.convertToPaginatedResponse(listOfApplications, pageableApplications);
            return ResponseEntity.ok(response);
        }

        log.info("Job ID: {} does not exists", id);
        return ResponseEntity.badRequest().build();
    }

    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<byte[]> getResume(Long applicationId){
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApplicationNotFoundException("No application found for Application ID: "+applicationId));
        User user = jwtUtil.getUserFromToken();
        if(application.getJob().getPostedBy().equals(user)) {
            byte[] resume = application.getApplicant().getResume();
            return ResponseEntity.ok()
                    .contentType(MediaType.valueOf(application.getApplicant().getFileType()))
                    .body(resume);
        }
        log.warn("Application ID: {} not found", applicationId);
        return ResponseEntity.badRequest().build();
    }

    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<String> changeApplicationStatus(String status, Long applicationId) {
        Application applicationEntity = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApplicationNotFoundException("No application found for Application ID: "+applicationId));
        User user = jwtUtil.getUserFromToken();

        if(!applicationEntity.getJob().getPostedBy().equals(user)){
            return ResponseEntity.badRequest().body("unauthorized user");
        }

        String currentStatus = applicationEntity.getStatus();
        if(currentStatus.equalsIgnoreCase(Constant.STATUS_REJECTED) || currentStatus.equalsIgnoreCase(Constant.STATUS_SHORTLISTED)) {
            return ResponseEntity.ok("This application is "+currentStatus+" already");
        }

        applicationEntity.setStatus(status.toUpperCase());
        applicationEntity.setUpdatedOn(LocalDateTime.now());
        Application applicationResponse = applicationRepository.save(applicationEntity);
        emailUtil.sendEmailNotification(applicationResponse.getApplicant().getUser().getEmail(), Constant.APPLICATION_STATUS_CHANGED,
                applicationResponse.getApplicant().getUser().getUsername(), Constant.APPLICATION_STATUS_CHANGED,
                applicationResponse.getJob().getJobTitle() , applicationResponse.getStatus());
        return ResponseEntity.ok("Status has been changed to "+status);
    }
}
