package com.dev.jobportal.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class PaginatedJobResponse {
    private List<JobResponseDto> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
}
