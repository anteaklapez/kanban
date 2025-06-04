package com.hivetech.kanban.repository;

import com.hivetech.kanban.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends CrudRepository<User, UUID> {
    User findByEmail(String email);
    boolean existsByEmail(String email);
}
