package com.hivetech.kanban.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.hivetech.kanban.dao.TaskDao;
import com.hivetech.kanban.model.Priority;
import com.hivetech.kanban.model.Status;
import com.hivetech.kanban.model.Task;
import com.hivetech.kanban.repository.TaskRepository;
import org.springframework.stereotype.Service;
import java.util.UUID;

// TODO: Validation!!

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Iterable<Task> getAllTasks(){
        return this.taskRepository.findAll();
    }

    public Task getTask(UUID id){
        return this.taskRepository.findById(id).get();
    }

    public Task createTask(TaskDao task){
        Task newTask = new Task();
        newTask.setTitle(task.getTitle());
        newTask.setDescription(task.getDescription());
        newTask.setStatus(Status.valueOf(task.getStatus().toUpperCase()));
        newTask.setPriority(Priority.valueOf(task.getPriority().toUpperCase()));

        this.taskRepository.save(newTask);
        return newTask;
    }

    public Task updateTask(UUID id, TaskDao task) {
        Task oldTask = this.taskRepository.findById(id).get();

        oldTask.setTitle(task.getTitle());
        oldTask.setDescription(task.getDescription());
        oldTask.setStatus(Status.valueOf(task.getStatus().toUpperCase()));
        oldTask.setPriority(Priority.valueOf(task.getPriority().toUpperCase()));

        this.taskRepository.save(oldTask);

        return oldTask;
    }

    public Task patchTask(UUID id, JsonPatch patch) throws JsonPatchException, JsonProcessingException {
        Task task = this.taskRepository.findById(id).get();

        Task taskPatched = applyPatchToTask(patch, task);
        this.taskRepository.save(taskPatched);

        return taskPatched;
    }

    public void deleteTask(UUID id){
        this.taskRepository.deleteById(id);
    }

    private Task applyPatchToTask(
            JsonPatch patch, Task targetTask) throws JsonPatchException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode patched = patch.apply(objectMapper.convertValue(targetTask, JsonNode.class));
        return objectMapper.treeToValue(patched, Task.class);
    }
}
