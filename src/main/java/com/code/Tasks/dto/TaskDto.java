package com.code.Tasks.dto;

import com.code.Tasks.entity.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto {
    private Long id;
    private String description;
    private long durationSeconds;
    private TaskStatus status;
    private Instant createdDate;
    private Instant modifiedDate;
}
