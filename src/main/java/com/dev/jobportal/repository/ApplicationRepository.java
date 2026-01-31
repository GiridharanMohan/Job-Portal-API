package com.dev.jobportal.repository;

import com.dev.jobportal.model.Application;
import com.dev.jobportal.model.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    Page<Application> findAllByJob(Pageable pageable, Job job);

    Optional<Application> findByApplicantIdAndJobId(Long applicationId, Long jobId);
}
