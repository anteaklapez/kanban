package com.hivetech.kanban.repository;

import com.hivetech.kanban.model.Task;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TaskRepository extends CrudRepository<Task, UUID> {
}
