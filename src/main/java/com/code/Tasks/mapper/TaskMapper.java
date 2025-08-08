package com.code.Tasks.mapper;

import com.code.Tasks.dto.CreateTaskRequest;
import com.code.Tasks.dto.TaskDto;
import com.code.Tasks.entity.Task;
import com.code.Tasks.entity.TaskStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class TaskMapper {
    public TaskDto mapToDto(Task task) {
        return new TaskDto(
                task.getId(),
                task.getDescription(),
                task.getDuration(),
                task.getStatus(),
                task.getCreatedDate(),
                task.getModifiedDate()
        );
    }

    public Task mapToTask(CreateTaskRequest req) {
        Instant now = Instant.now();
        return new Task(
                null, // id assigned by repository
                req.getDescription(),
                req.getDuration(),
                TaskStatus.IN_PROGRESS,
                now,
                now
        );
    }
}
