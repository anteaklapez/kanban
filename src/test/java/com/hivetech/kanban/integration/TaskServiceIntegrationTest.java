package com.hivetech.kanban.integration;

import com.hivetech.kanban.dto.TaskRequestDTO;
import com.hivetech.kanban.dto.TaskResponseDTO;
import com.hivetech.kanban.repository.TaskRepository;
import com.hivetech.kanban.service.TaskService;
import com.hivetech.kanban.util.TaskDTOMapperUtil;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TaskServiceIntegrationTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskDTOMapperUtil mapper;

    @Test
    void createTask_shouldPersistToDatabase() {
        TaskRequestDTO dto = new TaskRequestDTO();
        dto.setTitle("Integration Title");
        dto.setDescription("Integration Description");
        dto.setStatus("TO_DO");
        dto.setPriority("MED");

        TaskResponseDTO response = taskService.createTask(dto);

        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals("Integration Title", response.getTitle());

        assertTrue(taskRepository.findById(response.getId()).isPresent());
    }

    @Test
    void getTask_shouldReturnCorrectData() {
        TaskRequestDTO dto = new TaskRequestDTO();
        dto.setTitle("Integration Title");
        dto.setDescription("Integration Description");
        dto.setStatus("TO_DO");
        dto.setPriority("MED");

        TaskResponseDTO created = taskService.createTask(dto);

        TaskResponseDTO response = taskService.getTask(created.getId());

        assertNotNull(response);
        assertEquals("Integration Title", response.getTitle());
        assertEquals("Integration Description", response.getDescription());
    }

    @Test
    void updateTask_shouldModifyExisting() {
        TaskRequestDTO dto = new TaskRequestDTO();
        dto.setTitle("Integration Title");
        dto.setDescription("Integration Description");
        dto.setStatus("TO_DO");
        dto.setPriority("MED");

        TaskResponseDTO created = taskService.createTask(dto);

        TaskRequestDTO updateDto = new TaskRequestDTO();
        updateDto.setTitle("Updated Title");
        updateDto.setDescription("Updated Desc");
        updateDto.setStatus("IN_PROGRESS");
        updateDto.setPriority("HIGH");

        TaskResponseDTO updated = taskService.updateTask(created.getId(), updateDto);

        assertNotNull(updated);
        assertEquals("Updated Title", updated.getTitle());
        assertEquals("IN_PROGRESS", updated.getStatus());
        assertEquals("HIGH", updated.getPriority());
    }

    @Test
    void getAllTasks_shouldReturnPageWithAtLeastOne() {
        TaskRequestDTO dto = new TaskRequestDTO();
        dto.setTitle("Integration Title");
        dto.setDescription("Integration Description");
        dto.setStatus("TO_DO");
        dto.setPriority("MED");

        taskService.createTask(dto);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("priority").descending());
        Page<TaskResponseDTO> page = taskService.getAllTasks(null, pageable);

        assertTrue(page.getTotalElements() >= 1);
        TaskResponseDTO first = page.getContent().getFirst();
        assertNotNull(first.getTitle());
    }

    @Test
    void deleteTask_shouldRemoveFromDatabase() {
        TaskRequestDTO dto = new TaskRequestDTO();
        dto.setTitle("Integration Title");
        dto.setDescription("Integration Description");
        dto.setStatus("TO_DO");
        dto.setPriority("MED");

        TaskResponseDTO created = taskService.createTask(dto);

        taskService.deleteTask(created.getId());

        assertFalse(taskRepository.findById(created.getId()).isPresent());
    }
}
