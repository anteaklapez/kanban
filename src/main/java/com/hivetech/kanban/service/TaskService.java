package com.hivetech.kanban.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.hivetech.kanban.dto.TaskRequestDTO;
import com.hivetech.kanban.dto.TaskResponseDTO;
import com.hivetech.kanban.model.Priority;
import com.hivetech.kanban.model.Status;
import com.hivetech.kanban.model.Task;
import com.hivetech.kanban.repository.TaskRepository;
import com.hivetech.kanban.util.TaskDTOMapperUtil;
import com.hivetech.kanban.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.UUID;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskDTOMapperUtil taskDTOMapperUtil;

    public TaskService(TaskRepository taskRepository, TaskDTOMapperUtil taskDTOMapperUtil) {
        this.taskRepository = taskRepository;
        this.taskDTOMapperUtil = taskDTOMapperUtil;
    }

    public Page<TaskResponseDTO> getAllTasks(Status status, Pageable pageable){
        Page<Task> page = (status != null)
                ? taskRepository.findByStatus(status, pageable)
                : taskRepository.findAll(pageable);

        return page.map(taskDTOMapperUtil::toDTO);
    }

    public TaskResponseDTO getTask(UUID id) throws ResourceNotFoundException{
        Optional<Task> optionalTask = this.taskRepository.findById(id);
        if(optionalTask.isPresent()){
            Task task = optionalTask.get();
            return taskDTOMapperUtil.toDTO(task);
        } else
            throw new ResourceNotFoundException("Task with given ID does not exist.");
    }

    public TaskResponseDTO createTask(TaskRequestDTO task){
        Task newTask = new Task();
        newTask.setTitle(task.getTitle());
        newTask.setDescription(task.getDescription());
        newTask.setStatus(Status.valueOf(task.getStatus().toUpperCase()));
        newTask.setPriority(Priority.valueOf(task.getPriority().toUpperCase()));

        this.taskRepository.save(newTask);

        return taskDTOMapperUtil.toDTO(newTask);
    }

    public TaskResponseDTO updateTask(UUID id, TaskRequestDTO taskRequestDTO) throws ResourceNotFoundException{
        Optional<Task> optionalTask = this.taskRepository.findById(id);

        if(optionalTask.isPresent()){
            Task task = optionalTask.get();

            task.setTitle(taskRequestDTO.getTitle());
            task.setDescription(taskRequestDTO.getDescription());
            task.setStatus(Status.valueOf(taskRequestDTO.getStatus().toUpperCase()));
            task.setPriority(Priority.valueOf(taskRequestDTO.getPriority().toUpperCase()));

            this.taskRepository.save(task);

            return taskDTOMapperUtil.toDTO(task);
        } else
            throw new ResourceNotFoundException("Task with given ID does not exist.");
    }

    public TaskResponseDTO patchTask(UUID id, JsonPatch patch) throws JsonPatchException, JsonProcessingException, ResourceNotFoundException {
        Optional<Task> optionalTask = this.taskRepository.findById(id);

        if(optionalTask.isPresent()){
            Task taskPatched = applyPatchToTask(patch, optionalTask.get());

            this.taskRepository.save(taskPatched);

            return taskDTOMapperUtil.toDTO(taskPatched);
        } else
            throw new ResourceNotFoundException("Task with given ID does not exist.");
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
