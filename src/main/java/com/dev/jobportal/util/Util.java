package com.dev.jobportal.util;

import com.dev.jobportal.model.Application;
import com.dev.jobportal.model.Job;
import com.dev.jobportal.model.dto.ApplicationDto;
import com.dev.jobportal.model.dto.JobResponseDto;
import org.springframework.stereotype.Component;

@Component
public class Util {

    public JobResponseDto toJobResponseDto(Job job){
        JobResponseDto jobResponseDto = new JobResponseDto();
        jobResponseDto.setJobId(job.getId());
        jobResponseDto.setJobTitle(job.getJobTitle());
        jobResponseDto.setJobDescription(job.getJobDescription());
        jobResponseDto.setJobLocation(job.getJobLocation());
        jobResponseDto.setCompanyName(job.getCompanyName());
        jobResponseDto.setExperienceRequired(job.getExperienceRequired());
        jobResponseDto.setPostedOn(job.getPostedOn());
        jobResponseDto.setPostedBy(job.getPostedBy().getUsername());
        return jobResponseDto;
    }

    public ApplicationDto toApplicationDto(Application application){
        ApplicationDto applicationDto = new ApplicationDto();
        applicationDto.setApplicationId(application.getId());
        applicationDto.setJobId(application.getJob().getId());
        applicationDto.setJobTitle(application.getJob().getJobTitle());
        applicationDto.setApplicantId(application.getApplicant().getId());
        applicationDto.setApplicantName(application.getApplicant().getUser().getUsername());
        applicationDto.setResume(application.getApplicant().getResume());
        applicationDto.setApplicationStatus(application.getStatus());
        applicationDto.setAppliedOn(application.getAppliedOn());
        return applicationDto;
    }
}
