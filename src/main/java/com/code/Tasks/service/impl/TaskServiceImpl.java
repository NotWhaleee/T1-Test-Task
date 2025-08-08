package com.code.Tasks.service.impl;

import com.code.Tasks.dto.CreateTaskRequest;
import com.code.Tasks.dto.TaskDto;
import com.code.Tasks.entity.Task;
import com.code.Tasks.entity.TaskStatus;
import com.code.Tasks.exception.ResourceNotFoundException;
import com.code.Tasks.exception.TaskAlreadyDoneException;
import com.code.Tasks.mapper.TaskMapper;
import com.code.Tasks.repository.TaskRepository;
import com.code.Tasks.service.TaskService;
import jakarta.annotation.PreDestroy;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskMapper mapper;

    private final TaskRepository taskRepository;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);
    private final ConcurrentHashMap<Long, ScheduledFuture<?>> scheduledFutures = new ConcurrentHashMap<>();


    @Override
    public TaskDto createTask(CreateTaskRequest request) {
        Task task = mapper.mapToTask(request);
        Task saved = taskRepository.saveNew(task);

        ScheduledFuture<?> f = scheduler.schedule(() -> {
            taskRepository.update(saved.getId(), t -> {
                if (t.getStatus() == TaskStatus.IN_PROGRESS) {
                    t.setStatus(TaskStatus.DONE);
                    t.setModifiedDate(Instant.now());
                }
            });
            scheduledFutures.remove(saved.getId());
        }, saved.getDuration(), TimeUnit.SECONDS);

        scheduledFutures.put(saved.getId(), f);

        return mapper.mapToDto(saved);
    }

    @Override
    public TaskDto getTaskById(Long id) {
        Task t = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task " + id + " not found"));
        return mapper.mapToDto(t);
    }

    @Override
    public List<TaskDto> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(mapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void cancelTask(Long id) {
        Task t = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task " + id + " not found"));
        if (t.getStatus() == TaskStatus.DONE) {
            throw new TaskAlreadyDoneException("Task " + id + " already dome and cant be canceled");
        }
        // cancel scheduled transition if present
        ScheduledFuture<?> future = scheduledFutures.remove(id);
        if (future != null) {
            future.cancel(false);
        }

        taskRepository.update(id, task -> {
            task.setStatus(TaskStatus.CANCELED);
            task.setModifiedDate(Instant.now());
        });
    }

    @PreDestroy
    public void shutdownScheduler() {
        scheduler.shutdownNow();
    }
}
