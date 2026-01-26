package com.dev.jobportal.repository;

import com.dev.jobportal.model.Job;
import com.dev.jobportal.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    Page<Job> findAllByPostedBy(Pageable pageable, User user);

    Page<Job> findAllByJobStatus(Pageable pageable, String status);
}
