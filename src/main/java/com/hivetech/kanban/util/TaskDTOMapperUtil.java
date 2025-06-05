package com.hivetech.kanban.util;

import com.hivetech.kanban.dto.TaskResponseDTO;
import com.hivetech.kanban.model.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskDTOMapperUtil implements DTOMapperUtil<TaskResponseDTO, Task>{
    public TaskResponseDTO toDTO(Task task){
        TaskResponseDTO taskResponseDTO = new TaskResponseDTO();

        taskResponseDTO.setId(task.getId());
        taskResponseDTO.setTitle(task.getTitle());
        taskResponseDTO.setDescription(task.getDescription());
        taskResponseDTO.setStatus(task.getStatus().name());
        taskResponseDTO.setPriority(task.getPriority().name());
        taskResponseDTO.setVersion(task.getVersion());

        return taskResponseDTO;
    }
}
