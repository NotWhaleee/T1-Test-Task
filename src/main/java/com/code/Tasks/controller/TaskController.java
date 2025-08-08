package com.code.Tasks.controller;

import com.code.Tasks.dto.CreateTaskRequest;
import com.code.Tasks.dto.TaskDto;
import com.code.Tasks.service.TaskService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@AllArgsConstructor
@RestController
@RequestMapping("/tasks")
public class TaskController {

    private TaskService taskService;

    //Add task REST API
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskDto createTask(@Valid @RequestBody CreateTaskRequest request) {
        return taskService.createTask(request);
    }

    //Get Task REST API
    @GetMapping("{id}")
    public TaskDto getTaskById(@PathVariable("id") Long taskId) {
        return taskService.getTaskById(taskId);
    }

    //Get all tasks REST API
    @GetMapping
    public List<TaskDto> getAllTasks() {
        return taskService.getAllTasks();
    }

    //Delete task REST API
    @DeleteMapping("{id}")
    public String cancelTask(@PathVariable Long id) {
        taskService.cancelTask(id);
        return "Task " + id + " canceled";
    }
}
