package com.hivetech.kanban.service;

import com.github.fge.jsonpatch.JsonPatch;
import com.hivetech.kanban.dto.TaskRequestDTO;
import com.hivetech.kanban.dto.TaskResponseDTO;
import com.hivetech.kanban.exception.ResourceNotFoundException;
import com.hivetech.kanban.model.Priority;
import com.hivetech.kanban.model.Status;
import com.hivetech.kanban.model.Task;
import com.hivetech.kanban.repository.TaskRepository;
import com.hivetech.kanban.util.TaskDTOMapperUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskServiceTest {

    @Mock private TaskRepository taskRepository;
    @Mock private TaskDTOMapperUtil mapper;
    @InjectMocks private TaskService taskService;

    private Task sampleTask;
    private TaskResponseDTO sampleDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        sampleTask = new Task();
        sampleTask.setId(UUID.randomUUID());
        sampleTask.setTitle("Title");
        sampleTask.setDescription("Desc");
        sampleTask.setStatus(Status.TO_DO);
        sampleTask.setPriority(Priority.LOW);
        sampleTask.setVersion(1);

        sampleDto = new TaskResponseDTO();
        sampleDto.setId(sampleTask.getId());
        sampleDto.setTitle("Title");
        sampleDto.setDescription("Desc");
        sampleDto.setStatus("TO_DO");
        sampleDto.setPriority("LOW");
        sampleDto.setVersion(1);
    }

    @Test
    void getAllTasks_withoutStatus_shouldReturnPageMapped() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Task> tasks = Collections.singletonList(sampleTask);
        Page<Task> page = new PageImpl<>(tasks, pageable, tasks.size());

        when(taskRepository.findAll(pageable)).thenReturn(page);
        when(mapper.toDTO(sampleTask)).thenReturn(sampleDto);

        Page<TaskResponseDTO> result = taskService.getAllTasks(null, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(sampleDto, result.getContent().get(0));
        verify(taskRepository).findAll(pageable);
    }

    @Test
    void getAllTasks_withStatus_shouldReturnFilteredPage() {
        Pageable pageable = PageRequest.of(0, 5);
        List<Task> tasks = Collections.singletonList(sampleTask);
        Page<Task> page = new PageImpl<>(tasks, pageable, tasks.size());

        when(taskRepository.findByStatus(Status.TO_DO, pageable)).thenReturn(page);
        when(mapper.toDTO(sampleTask)).thenReturn(sampleDto);

        Page<TaskResponseDTO> result = taskService.getAllTasks(Status.TO_DO, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(sampleDto, result.getContent().get(0));
        verify(taskRepository).findByStatus(Status.TO_DO, pageable);
    }

    @Test
    void getTask_existingId_shouldReturnDto() {
        UUID id = sampleTask.getId();
        when(taskRepository.findById(id)).thenReturn(Optional.of(sampleTask));
        when(mapper.toDTO(sampleTask)).thenReturn(sampleDto);

        TaskResponseDTO dto = taskService.getTask(id);

        assertEquals(sampleDto, dto);
        verify(taskRepository).findById(id);
    }

    @Test
    void getTask_nonExistingId_shouldThrow() {
        UUID id = UUID.randomUUID();
        when(taskRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.getTask(id));
    }

    @Test
    void createTask_shouldSaveAndReturnDto() {
        TaskRequestDTO request = new TaskRequestDTO();
        request.setTitle("New");
        request.setDescription("Desc");
        request.setStatus("TO_DO");
        request.setPriority("LOW");
        request.setVersion(1);

        Task toSave = new Task("New", "Desc", Status.TO_DO, Priority.LOW, 1);
        toSave.setId(UUID.randomUUID());

        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
            Task arg = invocation.getArgument(0);
            arg.setId(toSave.getId());
            return arg;
        });
        when(mapper.toDTO(any(Task.class))).thenReturn(sampleDto);

        TaskResponseDTO result = taskService.createTask(request);

        assertEquals(sampleDto, result);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void updateTask_existingId_shouldSaveAndReturnDto() {
        UUID id = sampleTask.getId();
        TaskRequestDTO request = new TaskRequestDTO();
        request.setTitle("Upd");
        request.setDescription("UpdDesc");
        request.setStatus("IN_PROGRESS");
        request.setPriority("HIGH");

        when(taskRepository.findById(id)).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);
        when(mapper.toDTO(any(Task.class))).thenReturn(sampleDto);

        TaskResponseDTO result = taskService.updateTask(id, request);

        assertEquals(sampleDto, result);
        verify(taskRepository).save(sampleTask);
    }

    @Test
    void updateTask_nonExistingId_shouldThrow() {
        UUID id = UUID.randomUUID();
        TaskRequestDTO request = new TaskRequestDTO();
        when(taskRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.updateTask(id, request));
    }

    @Test
    void deleteTask_shouldCallRepository() {
        UUID id = sampleTask.getId();
        taskService.deleteTask(id);
        verify(taskRepository).deleteById(id);
    }

    @Test
    void patchTask_nonExistingId_shouldThrow() {
        UUID id = UUID.randomUUID();
        JsonPatch patch = mock(JsonPatch.class);
        when(taskRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.patchTask(id, patch));
    }
}
