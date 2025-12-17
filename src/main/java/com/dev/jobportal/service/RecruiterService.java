package com.dev.jobportal.service;

import com.dev.jobportal.model.Application;
import com.dev.jobportal.model.Job;
import com.dev.jobportal.model.User;
import com.dev.jobportal.repository.ApplicationRepository;
import com.dev.jobportal.repository.JobRepository;
import com.dev.jobportal.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    public ResponseEntity<?> postJob(Job job) {
        User user = jwtUtil.getUserFromToken();
        job.setPostedBy(user);
        job.setPostedOn(LocalDateTime.now());
        jobRepository.save(job);
        return ResponseEntity.ok("Job posted successfully");
    }


    public ResponseEntity<List<Job>> getPostedJobs() {
        User user = jwtUtil.getUserFromToken();
        List<Job> jobsPostedByUser = jobRepository.findByPostedBy(user);
        return ResponseEntity.status(HttpStatus.OK).body(jobsPostedByUser);
    }

    public ResponseEntity<Job> getPostedJobById(Long id) {
        User user = jwtUtil.getUserFromToken();
        Optional<Job> job = jobRepository.findById(id);
        if (job.isPresent() && job.get().getPostedBy().getEmail().equals(user.getEmail())) {
            return ResponseEntity.ok(job.get());
        }
        return ResponseEntity.badRequest().build();
    }

    public ResponseEntity<String> deleteJobById(Long id) {
        User user = jwtUtil.getUserFromToken();
        Optional<Job> job = jobRepository.findById(id);
        if (job.isPresent() && job.get().getPostedBy().getEmail().equals(user.getEmail())) {
            jobRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body("Job post has been deleted successfully");
        }
        return ResponseEntity.badRequest().body("Invalid Job ID");
    }

    public ResponseEntity<List<Application>> getAllApplicationsForJobId(Long id, String jobTitle) {
        User user = jwtUtil.getUserFromToken();
        Optional<Job> job = jobRepository.findById(id);
        if (job.isPresent() && job.get().getPostedBy().getEmail().equals(user.getEmail()) && job.get().getJobTitle().equals(jobTitle)) {
            List<Application> listOfApplications = applicationRepository.findAllByJob(job.get());
            return ResponseEntity.ok(listOfApplications);
        }
        return ResponseEntity.badRequest().build();
    }
}
