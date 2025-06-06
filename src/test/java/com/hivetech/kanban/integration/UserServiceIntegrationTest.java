package com.hivetech.kanban.integration;

import com.hivetech.kanban.dto.AuthResponseDTO;
import com.hivetech.kanban.dto.LoginRequestDTO;
import com.hivetech.kanban.dto.RegisterRequestDTO;
import com.hivetech.kanban.repository.UserRepository;
import com.hivetech.kanban.service.UserService;
import com.hivetech.kanban.util.JwtUtil;
import com.hivetech.kanban.util.UserDTOMapperUtil;
import jakarta.persistence.EntityExistsException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDTOMapperUtil mapper;

    private static final String EMAIL = "test@example.com";

    @Test
    void registerUser_new_shouldSaveAndAuthenticate() {
        RegisterRequestDTO registerDTO = new RegisterRequestDTO();
        registerDTO.setFirstName("Test");
        registerDTO.setLastName("User");
        registerDTO.setEmail(EMAIL);
        registerDTO.setPassword("password");

        AuthResponseDTO response = userService.registerUser(registerDTO);

        assertNotNull(response);
        assertNotNull(response.getToken());
        assertEquals(EMAIL, response.getUser().getEmail());
        assertTrue(userRepository.existsByEmail(EMAIL));
    }

    @Test
    void registerUser_duplicateEmail_shouldThrow() {
        RegisterRequestDTO registerDTO = new RegisterRequestDTO();
        registerDTO.setFirstName("Test");
        registerDTO.setLastName("User");
        registerDTO.setEmail(EMAIL);
        registerDTO.setPassword("password");
        userService.registerUser(registerDTO);

        RegisterRequestDTO duplicateDTO = new RegisterRequestDTO();
        duplicateDTO.setFirstName("Test");
        duplicateDTO.setLastName("User");
        duplicateDTO.setEmail(EMAIL);
        duplicateDTO.setPassword("password");

        assertThrows(EntityExistsException.class, () -> userService.registerUser(duplicateDTO));
    }

    @Test
    void authenticateUser_correctCredentials_shouldReturnJwt() {
        RegisterRequestDTO registerDTO = new RegisterRequestDTO();
        registerDTO.setFirstName("Test");
        registerDTO.setLastName("User");
        registerDTO.setEmail(EMAIL);
        registerDTO.setPassword("password");
        userService.registerUser(registerDTO);

        LoginRequestDTO loginDTO = new LoginRequestDTO();
        loginDTO.setEmail(EMAIL);
        loginDTO.setPassword("password");

        AuthResponseDTO authResponse = userService.authenticateUser(loginDTO);
        assertNotNull(authResponse);
        assertNotNull(authResponse.getToken());

        String usernameFromToken = jwtUtil.extractUsername(authResponse.getToken());
        assertEquals(EMAIL, usernameFromToken);
    }

    @Test
    void authenticateUser_wrongPassword_shouldThrow() {
        RegisterRequestDTO registerDTO = new RegisterRequestDTO();
        registerDTO.setFirstName("Test");
        registerDTO.setLastName("User");
        registerDTO.setEmail(EMAIL);
        registerDTO.setPassword("password");
        userService.registerUser(registerDTO);

        LoginRequestDTO loginDTO = new LoginRequestDTO();
        loginDTO.setEmail(EMAIL);
        loginDTO.setPassword("wrongpassword");

        assertThrows(BadCredentialsException.class,
                () -> userService.authenticateUser(loginDTO));
    }
}
