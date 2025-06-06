package com.hivetech.kanban.service;

import com.hivetech.kanban.dto.AuthResponseDTO;
import com.hivetech.kanban.dto.LoginRequestDTO;
import com.hivetech.kanban.dto.RegisterRequestDTO;
import com.hivetech.kanban.dto.UserResponseDTO;
import com.hivetech.kanban.model.User;
import com.hivetech.kanban.repository.UserRepository;
import com.hivetech.kanban.util.JwtUtil;
import com.hivetech.kanban.util.UserDTOMapperUtil;
import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;
    @Mock private UserDTOMapperUtil mapper;
    @Spy @InjectMocks private UserService userService;

    private User sampleUser;
    private LoginRequestDTO loginRequest;
    private RegisterRequestDTO registerRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sampleUser = new User();
        sampleUser.setId(java.util.UUID.randomUUID());
        sampleUser.setFirstName("TestFirstName");
        sampleUser.setLastName("TestLastName");
        sampleUser.setEmail("test@example.com");
        sampleUser.setPassword("encoded");

        loginRequest = new LoginRequestDTO();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("plaintext");

        registerRequest = new RegisterRequestDTO();
        registerRequest.setFirstName("TestFirstName");
        registerRequest.setLastName("TestLastName");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("plaintext");
    }

    @Test
    void authenticateUser_validCredentials_shouldReturnAuthResponse() {
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("test@example.com")
                .password("encoded")
                .authorities(java.util.Collections.emptyList())
                .build();

        Authentication authMock = mock(Authentication.class);
        when(authMock.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authMock);

        when(jwtUtil.generateToken("test@example.com")).thenReturn("jwt-token");
        when(userRepository.findByEmail("test@example.com")).thenReturn(sampleUser);

        UserResponseDTO userDto = new UserResponseDTO();
        userDto.setEmail("test@example.com");
        when(mapper.toDTO(sampleUser)).thenReturn(userDto);

        AuthResponseDTO response = userService.authenticateUser(loginRequest);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("test@example.com", response.getUser().getEmail());
        verify(authenticationManager).authenticate(any());
        verify(jwtUtil).generateToken("test@example.com");
    }

    @Test
    void registerUser_newEmail_shouldSaveAndAuthenticate() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("plaintext")).thenReturn("encoded");

        doReturn(new AuthResponseDTO(null, "jwt-token")).when(userService).authenticateUser(any(LoginRequestDTO.class));
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);

        AuthResponseDTO response = userService.registerUser(registerRequest);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        verify(userRepository).existsByEmail("test@example.com");
        verify(passwordEncoder).encode("plaintext");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_existingEmail_shouldThrowEntityExists() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);
        assertThrows(EntityExistsException.class, () -> userService.registerUser(registerRequest));
        verify(userRepository).existsByEmail("test@example.com");
    }
}
