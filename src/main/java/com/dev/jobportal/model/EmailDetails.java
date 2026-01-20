package com.dev.jobportal.model;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class EmailDetails {
    private String subject;
    private String recipient;
    private String body;
    @Nullable
    private String attachment;
}
