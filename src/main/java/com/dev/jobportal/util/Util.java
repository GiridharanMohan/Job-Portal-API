package com.dev.jobportal.util;

import com.dev.jobportal.model.Application;
import com.dev.jobportal.model.Job;
import com.dev.jobportal.model.dto.ApplicationResponseDto;
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
        jobResponseDto.setJobStatus(job.getJobStatus());
        return jobResponseDto;
    }

    public ApplicationResponseDto toApplicationResponseDto(Application application){
        ApplicationResponseDto applicationResponseDto = new ApplicationResponseDto();
        applicationResponseDto.setApplicationId(application.getId());
        applicationResponseDto.setJobId(application.getJob().getId());
        applicationResponseDto.setJobTitle(application.getJob().getJobTitle());
        applicationResponseDto.setApplicantId(application.getApplicant().getId());
        applicationResponseDto.setApplicantName(application.getApplicant().getUser().getUsername());
        applicationResponseDto.setResumeFileName(application.getApplicant().getFileName());
        applicationResponseDto.setApplicationStatus(application.getStatus());
        applicationResponseDto.setAppliedOn(application.getAppliedOn());
        applicationResponseDto.setUpdatedOn(application.getUpdatedOn());
        return applicationResponseDto;
    }
}
