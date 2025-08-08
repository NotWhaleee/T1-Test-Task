package com.code.Tasks.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    private Long id;
    private String description;
    private long duration;
    private TaskStatus status;
    private Instant createdDate;
    private Instant modifiedDate;
}
