package com.hivetech.kanban.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TaskRequestDTO {
    @NotBlank
    @Size(min = 3, max = 50)
    private String title;
    @Size(max = 1000)
    private String description;
    @NotBlank
    private String status;
    @NotBlank
    private String priority;
    private int version;

    public TaskRequestDTO(String title, String description, String status, String priority, int version) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.version = version;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
