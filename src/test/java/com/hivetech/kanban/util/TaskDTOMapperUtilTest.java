package com.hivetech.kanban.util;

import com.hivetech.kanban.dto.TaskResponseDTO;
import com.hivetech.kanban.model.Priority;
import com.hivetech.kanban.model.Status;
import com.hivetech.kanban.model.Task;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TaskDTOMapperUtilTest {

    private final TaskDTOMapperUtil mapper = new TaskDTOMapperUtil();

    @Test
    void toDTO_shouldMapAllFieldsCorrectly() {
        UUID id = UUID.randomUUID();
        Task task = new Task();
        task.setId(id);
        task.setTitle("Test Title");
        task.setDescription("Test Description");
        task.setStatus(Status.TO_DO);
        task.setPriority(Priority.HIGH);
        task.setVersion(1);

        TaskResponseDTO dto = mapper.toDTO(task);

        assertNotNull(dto);
        assertEquals(id, dto.getId());
        assertEquals("Test Title", dto.getTitle());
        assertEquals("Test Description", dto.getDescription());
        assertEquals("TO_DO", dto.getStatus());
        assertEquals("HIGH", dto.getPriority());
        assertEquals(1, dto.getVersion());
    }
}

