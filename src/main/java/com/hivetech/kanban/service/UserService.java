package com.hivetech.kanban.service;

import com.hivetech.kanban.dto.AuthResponseDTO;
import com.hivetech.kanban.dto.LoginRequestDTO;
import com.hivetech.kanban.dto.RegisterRequestDTO;
import com.hivetech.kanban.model.User;
import com.hivetech.kanban.repository.UserRepository;
import com.hivetech.kanban.util.JwtUtil;
import com.hivetech.kanban.util.UserDTOMapperUtil;
import jakarta.persistence.EntityExistsException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtils;
    private final UserDTOMapperUtil userDTOMapperUtil;

    public UserService(AuthenticationManager authenticationManager, UserRepository userRepository, PasswordEncoder encoder, JwtUtil jwtUtils, UserDTOMapperUtil userDTOMapperUtil) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
        this.userDTOMapperUtil = userDTOMapperUtil;
    }

    public AuthResponseDTO authenticateUser(LoginRequestDTO credentials) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        credentials.getEmail(),
                        credentials.getPassword()
                )
        );
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtUtils.generateToken(userDetails.getUsername());
        User user = this.userRepository.findByEmail(credentials.getEmail());
        return new AuthResponseDTO(userDTOMapperUtil.toDTO(user), token);
    }

    public AuthResponseDTO registerUser(RegisterRequestDTO user) throws EntityExistsException{
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EntityExistsException("User already exists.");
        }

        User newUser = new User(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                encoder.encode(user.getPassword())
        );

        userRepository.save(newUser);

        return this.authenticateUser(new LoginRequestDTO(newUser.getEmail(), newUser.getPassword()));
    }
}
