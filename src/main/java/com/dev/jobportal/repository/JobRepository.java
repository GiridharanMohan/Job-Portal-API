package com.dev.jobportal.repository;

import com.dev.jobportal.model.Job;
import com.dev.jobportal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByPostedBy(User user);
}
