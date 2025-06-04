package com.hivetech.kanban.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.hivetech.kanban.dao.TaskDao;
import com.hivetech.kanban.model.Task;
import com.hivetech.kanban.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("")
    public ResponseEntity<Iterable<Task>> getAllTasks(){
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTask(@PathVariable UUID id){
        return ResponseEntity.ok(taskService.getTask(id));
    }

    @PostMapping("")
    public ResponseEntity<Task> createTask(@RequestBody TaskDao task){
        return ResponseEntity.ok(taskService.createTask(task));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> putTask(@PathVariable UUID id, @RequestBody TaskDao task) {
        return ResponseEntity.ok(taskService.updateTask(id, task));
    }

    @PatchMapping(path = "/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<Task> patchTask(@PathVariable UUID id, @RequestBody JsonPatch patch) throws JsonPatchException, JsonProcessingException {
        return ResponseEntity.ok(taskService.patchTask(id, patch));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID id) {
        taskService.deleteTask(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
