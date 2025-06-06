package com.hivetech.kanban.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.hivetech.kanban.dto.ErrorResponse;
import com.hivetech.kanban.dto.TaskRequestDTO;
import com.hivetech.kanban.dto.TaskResponseDTO;
import com.hivetech.kanban.model.Status;
import com.hivetech.kanban.dto.TaskWebSocketEvent;
import com.hivetech.kanban.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Tasks", description = "CRUD operations for Kanban tasks")
public class TaskController {
    private final TaskService taskService;
    private final SimpMessagingTemplate messagingTemplate;

    public TaskController(TaskService taskService, SimpMessagingTemplate messagingTemplate) {
        this.taskService = taskService;
        this.messagingTemplate = messagingTemplate;
    }

    @Operation(summary = "Get all tasks", description = "Retrieve paginated list of tasks with optional status filter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved tasks"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("")
    public ResponseEntity<Page<TaskResponseDTO>> getAllTasks(@RequestParam(required = false) Status status,
                                                             @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(taskService.getAllTasks(status, pageable));
    }

    @Operation(summary = "Get task by ID", description = "Retrieve a single task by its unique ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved task"),
            @ApiResponse(responseCode = "404", description = "Task not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> getTask(@PathVariable UUID id) {
        return ResponseEntity.ok(taskService.getTask(id));
    }

    @Operation(summary = "Create new task", description = "Create a new task in the Kanban board")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("")
    public ResponseEntity<TaskResponseDTO> createTask(@RequestBody TaskRequestDTO task) {
        // 1) Create the task
        TaskResponseDTO created = taskService.createTask(task);

        // 2) Emit WebSocket event
        messagingTemplate.convertAndSend("/topic/tasks",
                new TaskWebSocketEvent("CREATED", created));

        // 3) Return the created DTO
        return ResponseEntity.ok(created);
    }

    @Operation(summary = "Update task completely", description = "Perform a full update (PUT) of a task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task updated successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> putTask(@PathVariable UUID id, @RequestBody TaskRequestDTO task) {
        // 1) Perform the update
        TaskResponseDTO updated = taskService.updateTask(id, task);

        // 2) Emit WebSocket event
        messagingTemplate.convertAndSend("/topic/tasks",
                new TaskWebSocketEvent("UPDATED", updated));

        // 3) Return the updated DTO
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Patch task", description = "Perform partial update (PATCH) of a task using JSON Patch")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task patched successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid patch format", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping(path = "/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<TaskResponseDTO> patchTask(@PathVariable UUID id, @RequestBody JsonPatch patch)
            throws JsonPatchException, JsonProcessingException {
        // 1) Perform the patch
        TaskResponseDTO patched = taskService.patchTask(id, patch);

        // 2) Emit WebSocket event
        messagingTemplate.convertAndSend("/topic/tasks",
                new TaskWebSocketEvent("UPDATED", patched));

        // 3) Return the patched DTO
        return ResponseEntity.ok(patched);
    }

    @Operation(summary = "Delete task", description = "Delete a task by its unique ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Task deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID id) {
        // 1) Delete from service
        taskService.deleteTask(id);

        // 2) Emit WebSocket event (sending just the ID of the deleted task)
        messagingTemplate.convertAndSend("/topic/tasks",
                new TaskWebSocketEvent("DELETED", id));

        // 3) Return no-content
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
