package com.taskflow.service;

import com.taskflow.dto.PageResponse;
import com.taskflow.dto.TaskRequest;
import com.taskflow.dto.TaskResponse;
import com.taskflow.dto.TaskStats;
import com.taskflow.exception.ResourceNotFoundException;
import com.taskflow.model.Priority;
import com.taskflow.model.Task;
import com.taskflow.model.TaskStatus;
import com.taskflow.repository.TaskRepository;
import com.taskflow.repository.TaskSpecifications;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class TaskService {

    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public PageResponse<TaskResponse> list(TaskStatus status, Priority priority, String search, Pageable pageable) {
        Page<Task> page = repository.findAll(TaskSpecifications.filter(status, priority, search), pageable);
        return PageResponse.from(page.map(TaskResponse::from));
    }

    public TaskResponse get(Long id) {
        return TaskResponse.from(find(id));
    }

    @Transactional
    public TaskResponse create(TaskRequest request) {
        Task task = new Task();
        apply(task, request);
        return TaskResponse.from(repository.save(task));
    }

    @Transactional
    public TaskResponse update(Long id, TaskRequest request) {
        Task task = find(id);
        apply(task, request);
        return TaskResponse.from(repository.save(task));
    }

    @Transactional
    public TaskResponse updateStatus(Long id, TaskStatus status) {
        Task task = find(id);
        task.setStatus(status);
        return TaskResponse.from(repository.save(task));
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(find(id));
    }

    public TaskStats stats() {
        long total = repository.count();

        Map<String, Long> byStatus = new LinkedHashMap<>();
        for (TaskStatus s : TaskStatus.values()) {
            byStatus.put(s.name(), repository.countByStatus(s));
        }

        Map<String, Long> byPriority = new LinkedHashMap<>();
        for (Priority p : Priority.values()) {
            byPriority.put(p.name(), repository.countByPriority(p));
        }

        long overdue = repository.countByDueDateBeforeAndStatusNot(LocalDate.now(), TaskStatus.DONE);
        long done = byStatus.get(TaskStatus.DONE.name());
        double rate = total == 0 ? 0 : Math.round(done * 1000.0 / total) / 10.0;

        return new TaskStats(total, byStatus, byPriority, overdue, rate);
    }

    private Task find(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tâche", id));
    }

    private void apply(Task task, TaskRequest request) {
        task.setTitle(request.title().trim());
        task.setDescription(request.description());
        task.setDueDate(request.dueDate());
        if (request.status() != null) {
            task.setStatus(request.status());
        }
        if (request.priority() != null) {
            task.setPriority(request.priority());
        }
    }
}