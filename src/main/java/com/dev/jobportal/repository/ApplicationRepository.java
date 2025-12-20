package com.dev.jobportal.repository;

import com.dev.jobportal.model.Application;
import com.dev.jobportal.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findAllByJob(Job job);

    Optional<Application> findByApplicantIdAndJobId(UUID applicationId, Long jobId);
}
