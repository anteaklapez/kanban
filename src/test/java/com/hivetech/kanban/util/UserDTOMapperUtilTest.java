package com.hivetech.kanban.util;

import com.hivetech.kanban.model.User;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserDTOMapperUtilTest {

    private final UserDTOMapperUtil mapper = new UserDTOMapperUtil();

    @Test
    void toDTO_shouldMapAllFieldsCorrectly() {
        UUID id = UUID.randomUUID();
        User user = new User();
        user.setId(id);
        user.setFirstName("Ana");
        user.setLastName("Kovač");
        user.setEmail("ana.kovac@example.com");

        com.hivetech.kanban.dto.UserResponseDTO dto = mapper.toDTO(user);

        assertNotNull(dto);
        assertEquals(id, dto.getId());
        assertEquals("Ana", dto.getFirstName());
        assertEquals("Kovač", dto.getLastName());
        assertEquals("ana.kovac@example.com", dto.getEmail());
    }
}

