package com.code.Tasks.repository;

import com.code.Tasks.entity.Task;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class TaskRepository {

    private final ConcurrentHashMap<Long, Task> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public Task saveNew(Task task) {
        long id = idGenerator.getAndIncrement();
        task.setId(id);
        store.put(id, task);
        return task;
    }

    public Optional<Task> findById(Long id) {
        Task t = store.get(id);
        return Optional.ofNullable(t);
    }

    public List<Task> findAll() {
        return new ArrayList<>(store.values());
    }

    public void update(Long id, TaskUpdater updater) {
        store.computeIfPresent(id, (k, old) -> {
            Task copy = new Task(old.getId(), old.getDescription(), old.getDuration(),
                    old.getStatus(), old.getCreatedDate(), old.getModifiedDate());
            updater.update(copy);
            return copy;
        });
    }

    @FunctionalInterface
    public interface TaskUpdater {
        void update(Task t);
    }
}
