package com.dev.jobportal.service;

import com.dev.jobportal.model.Job;
import com.dev.jobportal.model.User;
import com.dev.jobportal.model.dto.ApplicationDto;
import com.dev.jobportal.model.dto.JobResponseDto;
import com.dev.jobportal.repository.ApplicationRepository;
import com.dev.jobportal.repository.JobRepository;
import com.dev.jobportal.util.JwtUtil;
import com.dev.jobportal.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<String> deleteJobById(Long id) {
        User user = jwtUtil.getUserFromToken();
        Optional<Job> job = jobRepository.findById(id);
        if (job.isPresent() && job.get().getPostedBy().getEmail().equals(user.getEmail())) {
            jobRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body("Job post has been deleted successfully");
        }
        return ResponseEntity.badRequest().body("Invalid Job ID");
    }

    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<List<ApplicationDto>> getAllApplicationsForJobId(Long id, String jobTitle) {
        User user = jwtUtil.getUserFromToken();
        Optional<Job> job = jobRepository.findById(id);
        if (job.isPresent() && job.get().getPostedBy().getEmail().equals(user.getEmail()) && job.get().getJobTitle().equals(jobTitle)) {
            List<ApplicationDto> listOfApplications = applicationRepository.findAllByJob(job.get())
                    .stream().map(application -> util.toApplicationDto(application)).toList();
            return ResponseEntity.ok(listOfApplications);
        }
        return ResponseEntity.badRequest().build();
    }
}
