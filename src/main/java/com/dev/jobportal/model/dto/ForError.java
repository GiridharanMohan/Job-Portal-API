package com.dev.jobportal.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
public class ForError {
    Map<String, String > errors;
    LocalDateTime timestamp;

    public ForError(Map<String, String> errors){
        this.errors = errors;
        this.timestamp = LocalDateTime.now();
    }

}
