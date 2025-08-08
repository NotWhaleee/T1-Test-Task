package com.code.Tasks.integrations;

import com.code.Tasks.dto.CreateTaskRequest;
import com.code.Tasks.dto.TaskDto;
import com.code.Tasks.entity.TaskStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TaskIntegrationTest {

    @Autowired
    private TestRestTemplate rest;

    @Test
    public void fullScenario_create_list_wait_done_get_and_cannot_cancel() throws InterruptedException {
        // Create a task with short duration
        CreateTaskRequest req = new CreateTaskRequest("integration test", 1L);

        ResponseEntity<TaskDto> createResp = rest.postForEntity("/tasks", req, TaskDto.class);
        assertThat(createResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        TaskDto created = createResp.getBody();
        assertThat(created).isNotNull();
        Long id = created.getId();
        assertThat(created.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);

        // Get list and check that the task is present
        ResponseEntity<TaskDto[]> listResp = rest.getForEntity("/tasks", TaskDto[].class);
        assertThat(listResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        TaskDto[] tasks = listResp.getBody();
        assertThat(tasks).isNotNull();
        assertThat(Arrays.stream(tasks).anyMatch(t -> t.getId().equals(id))).isTrue();

        // Wait until task expired
        Thread.sleep(1500);

        // Get by id and check status is DONE
        ResponseEntity<TaskDto> getResp = rest.getForEntity("/tasks/" + id, TaskDto.class);
        assertThat(getResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        TaskDto byId = getResp.getBody();
        assertThat(byId).isNotNull();
        assertThat(byId.getStatus()).isEqualTo(TaskStatus.DONE);

        // Try to cancel
        // should receive 400 Bad Request
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<String> deleteResp = rest.exchange("/tasks/" + id, HttpMethod.DELETE, entity, String.class);
        assertThat(deleteResp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
