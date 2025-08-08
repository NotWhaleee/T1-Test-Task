package com.code.Tasks.service;

import com.code.Tasks.dto.CreateTaskRequest;
import com.code.Tasks.dto.TaskDto;

import java.util.List;

public interface TaskService {
    TaskDto createTask(CreateTaskRequest request);

    TaskDto getTaskById(Long id);

    List<TaskDto> getAllTasks();

    void cancelTask(Long id);
}
